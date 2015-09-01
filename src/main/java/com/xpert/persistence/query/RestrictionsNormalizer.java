package com.xpert.persistence.query;

import com.xpert.utils.ReflectionUtils;
import com.xpert.i18n.I18N;
import com.xpert.utils.StringUtils;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.persistence.TemporalType;

/**
 *
 * @author ayslan
 */
public class RestrictionsNormalizer {

    public static final String DATE_FILTER_INTERVAL_SEPARATOR = " ## ";
    private static final Logger logger = Logger.getLogger(QueryBuilder.class.getName());

    public static List<Restriction> getNormalizedRestrictions(Class from, List<Restriction> restrictions, String alias) {

        List<Restriction> normalizedRestrictions = new ArrayList<Restriction>();
        //normalize result
        for (Restriction originalRestriction : restrictions) {
            boolean ignoreRestriction = false;
            List<Restriction> moreRestrictions = new ArrayList<Restriction>();

            //copy and create a new restriction
            Restriction restriction = new Restriction();
            restriction.setLikeType(originalRestriction.getLikeType());
            restriction.setProperty(originalRestriction.getProperty());
            restriction.setRestrictionType(originalRestriction.getRestrictionType());
            restriction.setTemporalType(originalRestriction.getTemporalType());
            restriction.setValue(originalRestriction.getValue());
            restriction.setParameters(originalRestriction.getParameters());
            restriction.setIlike(originalRestriction.isIlike());

            //if RestrictionType is null set default to EQUALS
            if (restriction.getRestrictionType() == null) {
                restriction.setRestrictionType(RestrictionType.EQUALS);
            }

            //DataTable filter has his own logic
            if (restriction.getRestrictionType().equals(RestrictionType.DATA_TABLE_FILTER)) {
                String property = "";
                if (alias != null && !alias.trim().isEmpty() && restriction.getProperty().indexOf(".") > -1) {
                    property = restriction.getProperty().substring(restriction.getProperty().indexOf(".") + 1, restriction.getProperty().length());
                } else {
                    property = restriction.getProperty();
                }

                Class propertyType = String.class;
                try {
                    //try to get type from property
                    propertyType = ReflectionUtils.getPropertyType(from, property);
                } catch (IllegalArgumentException ex) {
                    //type cannot be get, keep String type
                }

                //set to type to EQUALS when is not String
                if (!propertyType.equals(String.class)) {
                    restriction.setRestrictionType(RestrictionType.EQUALS);
                    if (restriction.getValue().toString() != null && !restriction.getValue().toString().isEmpty()) {
                        if (propertyType.isEnum()) {
                            restriction.setValue(Enum.valueOf(propertyType, restriction.getValue().toString()));
                        }
                        if (propertyType.equals(Integer.class) || propertyType.equals(int.class)
                                || propertyType.equals(Short.class) || propertyType.equals(short.class)
                                || propertyType.equals(BigInteger.class)) {
                            String valueNumber = StringUtils.getOnlyIntegerNumbers(restriction.getValue().toString());
                            if (valueNumber != null && !valueNumber.isEmpty()) {
                                if (propertyType.equals(Integer.class) || propertyType.equals(int.class)) {
                                    restriction.setValue(Integer.valueOf(valueNumber));
                                } else if (propertyType.equals(Short.class) || propertyType.equals(short.class)) {
                                    restriction.setValue(Short.valueOf(valueNumber));
                                } else if (propertyType.equals(BigInteger.class)) {
                                    restriction.setValue(new BigInteger(valueNumber));
                                }
                            } else {
                                ignoreRestriction = true;
                            }
                        }
                        if (propertyType.equals(Long.class) || propertyType.equals(long.class)) {
                            String valueNumber = StringUtils.getOnlyIntegerNumbers(restriction.getValue().toString());
                            if (valueNumber != null && !valueNumber.isEmpty()) {
                                restriction.setValue(Long.valueOf(valueNumber));
                            } else {
                                ignoreRestriction = true;
                            }
                        }
                        if (propertyType.equals(BigDecimal.class)) {
                            restriction.setValue(new BigDecimal(restriction.getValue().toString()));
                        }
                        if (propertyType.equals(Boolean.class) || propertyType.equals(boolean.class)) {
                            restriction.setValue(Boolean.valueOf(restriction.getValue().toString()));
                        }
                    }
                    //if is a date, then its a interval, set GREATER THAN and LESS THAN
                    if (propertyType.equals(Date.class) || propertyType.equals(Calendar.class)) {

                        SimpleDateFormat dateFormat = new SimpleDateFormat(I18N.getDatePattern(), I18N.getLocale());
                        Object value = restriction.getValue().toString();
                        String[] dateArray = null;
                        if (value != null) {
                            dateArray = value.toString().split(DATE_FILTER_INTERVAL_SEPARATOR);
                        }
                        String startDateString = null;
                        String endDateString = null;
                        if (dateArray != null && dateArray.length > 0) {
                            startDateString = dateArray[0];
                            if (dateArray.length > 1) {
                                endDateString = dateArray[1];
                            }
                        }
                        try {
                            //if start date is empty then should be ignored
                            if (startDateString != null && !startDateString.isEmpty()) {
                                restriction.setValue(dateFormat.parse(startDateString.trim()));
                                restriction.setTemporalType(TemporalType.TIMESTAMP);
                                restriction.setRestrictionType(RestrictionType.GREATER_EQUALS_THAN);
                            } else {
                                ignoreRestriction = true;
                            }
                            //add LESS THAN
                            if (endDateString != null && !endDateString.trim().isEmpty()) {
                                Date dateEnd = dateFormat.parse(endDateString.trim());
                                //add 1 day e set to the first second
                                Calendar calendar = (Calendar) Calendar.getInstance().clone();
                                calendar.setTime(dateEnd);
                                calendar.add(Calendar.DATE, 1);
                                calendar.set(Calendar.HOUR_OF_DAY, 0);
                                calendar.set(Calendar.MINUTE, 0);
                                calendar.set(Calendar.SECOND, 0);
                                calendar.set(Calendar.MILLISECOND, 0);
                                dateEnd = calendar.getTime();
                                moreRestrictions.add(new Restriction(restriction.getProperty(), RestrictionType.LESS_THAN, dateEnd));
                            }
                        } catch (ParseException ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    }
                } else {
                    restriction.setRestrictionType(RestrictionType.LIKE);
                }

            }
            if (ignoreRestriction == false) {
                normalizedRestrictions.add(restriction);
            }
            if (moreRestrictions != null && !moreRestrictions.isEmpty()) {
                normalizedRestrictions.addAll(moreRestrictions);
            }
        }

        return normalizedRestrictions;
    }
}
