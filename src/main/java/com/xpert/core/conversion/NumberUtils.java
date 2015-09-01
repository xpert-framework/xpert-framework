package com.xpert.core.conversion;

import com.xpert.i18n.I18N;
import java.lang.reflect.InvocationTargetException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.NumberFormat;
import java.util.Collection;
import org.apache.commons.beanutils.NestedNullException;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Utility class to manipulate Number.
 *
 * @author Ayslan
 */
public class NumberUtils {

    public static String convertToMoney(BigDecimal valor) {

        NumberFormat nf = NumberFormat.getCurrencyInstance(I18N.getLocale());
        String retorno = nf.format(valor);

        return retorno;
    }

    public static String convertToNumber(BigDecimal valor) {

        NumberFormat numberFormat = getNumberFormat();
        String retorno = numberFormat.format(valor);

        return retorno;
    }

    public static String convertToMoney(Double valor) {

        NumberFormat nf = NumberFormat.getCurrencyInstance(I18N.getLocale());
        String retorno = nf.format(valor);

        return retorno;
    }

    public static String convertToNumber(Double valor) {

        NumberFormat numberFormat = getNumberFormat();
        String retorno = numberFormat.format(valor);

        return retorno;
    }

    public static String convertToNumber(String valor) {

        NumberFormat numberFormat = getNumberFormat();
        String retorno = numberFormat.format(new BigDecimal(valor));

        return retorno;
    }

    public static NumberFormat getNumberFormat() {
        return getNumberFormat(2, 2);
    }

    public static NumberFormat getNumberFormat(int maximumFractionDigits, int minimumFractionDigits) {

        NumberFormat numberFormat = NumberFormat.getNumberInstance(I18N.getLocale());
        numberFormat.setMaximumFractionDigits(maximumFractionDigits);
        numberFormat.setMinimumFractionDigits(minimumFractionDigits);

        return numberFormat;
    }

    /**
     * Returns a BigDecimal instance o of sum fields in collection. If
     * collection is null or empty return ZERO. If sum is null, return ZERO.
     *
     *
     * @param objects
     * @param field
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static BigDecimal sum(Collection objects, String field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        BigDecimal total = BigDecimal.ZERO;
        if (objects == null || objects.isEmpty()) {
            return total;
        }

        if (field != null && !field.trim().isEmpty()) {
            for (Object o : objects) {
                Object value = null;
                try {
                    value = PropertyUtils.getProperty(o, field);
                } catch (NestedNullException ex) {
                    //nested null, do nothing
                }
                if (value != null) {
                    total = total.add(convertToBigDecimal(value));
                }
            }
        }

        return total;

    }

    /**
     * Returns a BigDecimal instance of average of fields in collection. If
     * collection is null or empty return ZERO. If average is null, return ZERO.
     *
     *
     * @param objects
     * @param field
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static BigDecimal avg(Collection objects, String field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        BigDecimal total = BigDecimal.ZERO;
        if (objects == null || objects.isEmpty()) {
            return total;
        }

        int count = 0;
        if (field != null && !field.trim().isEmpty()) {
            for (Object o : objects) {
                Object value = null;
                try {
                    value = PropertyUtils.getProperty(o, field);
                } catch (NestedNullException ex) {
                    //nested null, do nothing
                }
                if (value != null) {
                    count++;
                    total = total.add(convertToBigDecimal(value));
                }
            }
        }

        if (count == 0) {
            return BigDecimal.ZERO;
        }

        return total.divide(new BigDecimal(count), 2, RoundingMode.HALF_UP);

    }

    /**
     * Returns a BigDecimal instance of max value of fields in collection. If
     * collection is null or empty return null.
     *
     *
     * @param objects
     * @param field
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static BigDecimal max(Collection objects, String field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        if (objects == null || objects.isEmpty()) {
            return null;
        }
        BigDecimal max = null;
        if (field != null && !field.trim().isEmpty()) {
            for (Object o : objects) {
                Object value = null;
                BigDecimal current = null;
                try {
                    value = PropertyUtils.getProperty(o, field);
                } catch (NestedNullException ex) {
                    //nested null, do nothing
                }
                if (value != null) {
                    current = convertToBigDecimal(value);
                    if (max == null || current.compareTo(max) > 0) {
                        max = current;
                    }
                }
            }
        }
        return max;
    }

    /**
     * Returns a BigDecimal instance of min value of fields in collection. If
     * collection is null or empty return null.
     *
     *
     * @param objects
     * @param field
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static BigDecimal min(Collection objects, String field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        if (objects == null || objects.isEmpty()) {
            return null;
        }
        BigDecimal min = null;
        if (field != null && !field.trim().isEmpty()) {
            for (Object o : objects) {
                Object value = null;
                BigDecimal current = null;
                try {
                    value = PropertyUtils.getProperty(o, field);
                } catch (NestedNullException ex) {
                    //nested null, do nothing
                }
                if (value != null) {
                    current = convertToBigDecimal(value);
                    if (min == null || current.compareTo(min) < 0) {
                        min = current;
                    }
                }
            }
        }
        return min;
    }

    /**
     * Try to convert a object to BigDecimal Example: Double = new
     * BigDecimal((Double) value) Float = new BigDecimal((Float) value)
     *
     * @param value
     * @return
     */
    public static BigDecimal convertToBigDecimal(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof BigDecimal) {
            return ((BigDecimal) value);
        } else if (value instanceof BigInteger) {
            return new BigDecimal((BigInteger) value);
        } else if (value instanceof Double) {
            return new BigDecimal((Double) value);
        } else if (value instanceof Float) {
            return new BigDecimal((Float) value);
        } else if (value instanceof Number) {
            Number numberValue = (Number) value;
            return new BigDecimal(numberValue.longValue());
        } else {
            throw new IllegalArgumentException("Canoot convert " + value.getClass().getName() + " to " + BigDecimal.class.getName() + ". Value: " + value);
        }
    }

    /**
     * Returns a Integer instance o of sum fields in collection. If collection
     * is null or empty return ZERO. If sum is null, return ZERO.
     *
     *
     * @param objects
     * @param field
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Integer sumInteger(Collection objects, String field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Integer total = 0;
        if (objects == null || objects.isEmpty()) {
            return total;
        }
        if (field != null && !field.trim().isEmpty()) {
            for (Object o : objects) {
                Object value = null;
                try {
                    value = PropertyUtils.getProperty(o, field);
                } catch (NestedNullException ex) {
                    //nested null, do nothing
                }
                if (value != null) {
                    if (value instanceof Number) {
                        total = total + ((Number) value).intValue();
                    }
                }
            }
        }

        return total;

    }

    /**
     * Returns a Integer instance o of sum fields in collection. If collection
     * is null or empty return ZERO. If sum is null, return ZERO.
     *
     *
     * @param objects
     * @param field
     * @return
     * @throws NoSuchMethodException
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     */
    public static Long sumLong(Collection objects, String field) throws NoSuchMethodException, IllegalAccessException, InvocationTargetException {

        Long total = 0L;

        if (objects == null || objects.isEmpty()) {
            return total;
        }
        if (field != null && !field.trim().isEmpty()) {
            for (Object o : objects) {
                Object value = null;
                try {
                    value = PropertyUtils.getProperty(o, field);
                } catch (NestedNullException ex) {
                    //nested null, do nothing
                }
                if (value != null) {
                    if (value instanceof Number) {
                        total = total + ((Number) value).longValue();
                    }
                }
            }
        }

        return total;

    }

}
