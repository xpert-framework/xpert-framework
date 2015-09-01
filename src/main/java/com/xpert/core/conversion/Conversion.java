package com.xpert.core.conversion;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Conversion {

    public static String dateToString(Date date, String pattern) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dt = new SimpleDateFormat(pattern);
        return dt.format(date);
    }

    public static Date stringToDate(String string, String pattern) throws Exception {
        SimpleDateFormat form = new SimpleDateFormat(pattern);
        return form.parse(string);
    }

    public static Double stringToDouble(String valor) {
        valor = valor.replace(".", "").replace(",", ".");
        return Double.parseDouble(valor);
    }

    public static BigDecimal stringToBigDecimal(String valor) {
        return new BigDecimal(stringToDouble(valor));
    }

    public static String removeAccent(String value) {
        if (value != null && !(value.trim().equals(""))) {
            char[] accents = new char[]{'ç', 'á', 'à', 'ã', 'â', 'ä', 'é', 'è', 'ê', 'ë', 'í', 'ì', 'î', 'ï', 'ó', 'ò', 'õ', 'ô', 'ö', 'ú', 'ù', 'û', 'ü'};
            char[] noAccents = new char[]{'c', 'a', 'a', 'a', 'a', 'a', 'e', 'e', 'e', 'e', 'i', 'i', 'i', 'i', 'o', 'o', 'o', 'o', 'o', 'u', 'u', 'u', 'u'};
            for (int i = 0; i < accents.length; i++) {
                value = value.replace(accents[i], noAccents[i]);
                value = value.replace(Character.toUpperCase(accents[i]), Character.toUpperCase(noAccents[i]));
            }
            return value;
        }
        return value;
    }
}
