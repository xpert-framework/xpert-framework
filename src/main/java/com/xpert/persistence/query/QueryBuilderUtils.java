package com.xpert.persistence.query;

import static com.xpert.persistence.query.Aggregate.*;
import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.beanutils.PropertyUtils;

/**
 *
 * @author ayslanms
 */
public class QueryBuilderUtils {

    private static final Logger logger = Logger.getLogger(QueryBuilderUtils.class.getName());

    /**
     * Returns a string based on QueryType.
     *
     * <ul>
     * <li>MAX returns a SELECT MAX(field)</li>
     * <li>MIN returns a SELECT MIN(field)</li>
     * <li>AVG returns a SELECT AVG(field)</li>
     * <li>COUNT returns a SELECT COUNT(field)/COUNT(*)</li>
     * <li>SELECT type returns "SELECT <code>select</code>" if
     * <code>select</code> is emptu then returns a empty String</li>
     * </ul>
     *
     * @param type
     * @param select
     * @param aggregate
     * @return
     */
    public static String getQuerySelectClausule(QueryType type, String select, String aggregate) {

        if (type == null) {
            return "";
        }

        StringBuilder queryString = new StringBuilder();

        if (type.equals(QueryType.COUNT)) {
            if (select != null && !select.trim().isEmpty()) {
                queryString.append("SELECT ").append(count(select));
            } else {
                queryString.append("SELECT ").append(count("*"));
            }
        } else if (type.equals(QueryType.MAX)) {
            queryString.append("SELECT ").append(max(select));
        } else if (type.equals(QueryType.MIN)) {
            queryString.append("SELECT ").append(min(select));
        } else if (type.equals(QueryType.SUM)) {
            queryString.append("SELECT ").append(sum(select));
        } else if (type.equals(QueryType.AVG)) {
            queryString.append("SELECT ").append(avg(select));
        } else if (type.equals(QueryType.SELECT) && (select != null && !select.isEmpty())) {
            queryString.append("SELECT ").append(select).append(" ");
            if (aggregate != null && !aggregate.isEmpty()) {
                queryString.append(", ").append(aggregate).append(" ");
            }
        }

        return queryString.toString();

    }

    /**
     * @param <T> Result Type
     * @param attributes Attributes to select
     * @param resultList The Query Result List
     * @param clazz The expected type in result
     * @return
     */
    public static <T> List<T> getNormalizedResultList(String attributes, List resultList, Class<T> clazz) {
        if (attributes != null && attributes.split(",").length > 0) {
            List result = new ArrayList();
            String[] fields = attributes.split(",");
            for (Object object : resultList) {
                try {
                    Object entity = clazz.newInstance();
                    for (int i = 0; i < fields.length; i++) {
                        String property = fields[i].trim().replaceAll("/s", "");
                        initializeCascade(property, entity);
                        if (object instanceof Object[]) {
                            PropertyUtils.setProperty(entity, property, ((Object[]) object)[i]);
                        } else {
                            PropertyUtils.setProperty(entity, property, object);
                        }
                    }
                    result.add(entity);
                } catch (Exception ex) {
                    throw new RuntimeException(ex);
                }
            }
            return result;
        }
        return resultList;
    }

    public static List<QueryParameter> getQueryParameters(List<Restriction> restrictions) {
        int position = 1;
        List<QueryParameter> queryParameters = new ArrayList<>();
        for (Restriction re : restrictions) {
            //add custom parameters
            if (re.getParameters() != null) {
                queryParameters.addAll(re.getParameters());
            }
            if (re.getRestrictionType().isIgnoreParameter()) {
                continue;
            }
            if (re.getValue() != null) {
                QueryParameter parameter = null;
                if (re.getRestrictionType().equals(RestrictionType.LIKE)) {
                    if (re.getLikeType() == null || re.getLikeType().equals(LikeType.BOTH)) {
                        parameter = new QueryParameter(position, "%" + re.getValue() + "%");
                    } else if (re.getLikeType().equals(LikeType.BEGIN)) {
                        parameter = new QueryParameter(position, re.getValue() + "%");
                    } else if (re.getLikeType().equals(LikeType.END)) {
                        parameter = new QueryParameter(position, "%" + re.getValue());
                    }
                } else {
                    if (re.getTemporalType() != null && (re.getValue() instanceof Date || re.getValue() instanceof Calendar)) {
                        parameter = new QueryParameter(position, re.getValue(), re.getTemporalType());
                    } else {
                        parameter = new QueryParameter(position, re.getValue());
                    }
                }
                queryParameters.add(parameter);
                position++;
            }
        }

        return queryParameters;
    }

    public static void initializeCascade(String property, Object bean) {
        int index = property.indexOf(".");
        if (index > -1) {
            try {
                String field = property.substring(0, property.indexOf("."));
                Object propertyToInitialize = PropertyUtils.getProperty(bean, field);
                if (propertyToInitialize == null) {
                    propertyToInitialize = PropertyUtils.getPropertyDescriptor(bean, field).getPropertyType().newInstance();
                    PropertyUtils.setProperty(bean, field, propertyToInitialize);
                }
                String afterField = property.substring(index + 1, property.length());
                if (afterField != null && afterField.indexOf(".") > -1) {
                    initializeCascade(afterField, propertyToInitialize);
                }
            } catch (Exception ex) {
                logger.log(Level.SEVERE, null, ex);
            }
        }
    }

    public static String readInputStreamAsString(InputStream inputStream)
            throws IOException {

        BufferedInputStream bis = new BufferedInputStream(inputStream);
        ByteArrayOutputStream buf = new ByteArrayOutputStream();
        int result = bis.read();
        while (result != -1) {
            byte b = (byte) result;
            buf.write(b);
            result = bis.read();
        }
        bis.close();
        buf.close();
        return buf.toString();
    }

}
