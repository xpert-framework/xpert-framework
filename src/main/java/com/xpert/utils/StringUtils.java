package com.xpert.utils;

import com.xpert.core.conversion.Conversion;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;

/**
 * Utility class to use with java.lang.String
 *
 * @author ayslan
 */
public class StringUtils implements Serializable {

    private static final long serialVersionUID = 3965126272187461019L;
    
    public final static String PATTERN_URL = "\\b(((ht|f)tp(s?)\\:\\/\\/|~\\/|\\/)|www.)"
            + "(\\w+:\\w+@)?(([-\\w]+\\.)+(jpg|jpeg|png|gif|com|org|net|gov"
            + "|mil|biz|info|mobi|name|aero|jobs|museum"
            + "|travel|[a-z]{2}))(:[\\d]{1,5})?"
            + "(((\\/([-\\w~!$+|.,=]|%[a-f\\d]{2})+)+|\\/)+|\\?|#)?"
            + "((\\?([-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
            + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)"
            + "(&(?:[-\\w~!$+|.,*:]|%[a-f\\d{2}])+=?"
            + "([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)*)*"
            + "(#([-\\w~!$+|.,*:=]|%[a-f\\d]{2})*)?\\b";

    /**
     * Search url in text and replace by anchor
     *
     * Example: http://www.test.com returns <a href="http://www.test.com"
     * target="_blank">http://www.test.com</a>
     *
     * @param message
     * @return
     */
    public static String replaceUrlByAnchor(String message) {
        String strRegex = "((((https?|ftp|telnet|file):((//)|(\\\\))+)|(www.))+[\\w\\d:#@%/;$()~_?\\+-=\\\\.&]*)";

        if (message != null) {
            StringBuffer str = new StringBuffer(message.length());
            Pattern pattern = Pattern.compile(strRegex);
            Matcher matcher = pattern.matcher(message);

            // Replace urls with anchor tags to the url
            while (matcher.find()) {
                String url = matcher.group(0);
                String httpURL = url;
                if (!url.substring(0, 4).equalsIgnoreCase("http")
                        && !url.substring(0, 3).equalsIgnoreCase("ftp")
                        && !url.substring(0, 4).equalsIgnoreCase("teln")
                        && !url.substring(0, 4).equalsIgnoreCase("file")) {
                    httpURL = "http://" + url;
                }

                matcher.appendReplacement(str, Matcher.quoteReplacement("" + "<a href=\"" + httpURL + "\" target=\"_blank\" >" + url + "</a>" + ""));
            }
            matcher.appendTail(str);
            message = str.toString();
        }
        return message;
    }

    /**
     * Remove HTML tags from string
     *
     * @param string
     * @return
     */
    public static String removeHTML(String string) {
        return string.replaceAll("\\<.*?\\>", "");
    }

    /**
     * Return only number, "," and "." characters of a String
     *
     * @param string
     * @return
     */
    public static String getOnlyDecimalNumbers(String string) {
        return string.replaceAll("([^0-9|^,|^.])", "");
    }

    /**
     * Return only number characters of String
     *
     * @param string
     * @return
     */
    public static String getOnlyIntegerNumbers(String string) {
        return string.replaceAll("([^0-9])", "");
    }

    /**
     * Return only aplhanumeric characters of a String
     *
     * @param string
     * @return
     */
    public static String getAlphaNumeric(String string) {
        return string.replaceAll("[^a-z0-9A-Z -]", "");
    }

    /**
     * Remove duplicate white spaces in String and trim() the result.
     *
     * Example: "This is a Example" return "This is a Example"
     *
     * @param string
     * @return
     */
    public static String removeDuplicateWhiteSpace(String string) {
        Pattern pattern = Pattern.compile("\\s{2,}");
        Matcher matcher = pattern.matcher(string);
        return matcher.replaceAll(" ").trim();
    }

    /**
     * Extract a String of URL pattern in String
     *
     * @param string
     * @return
     */
    public static String extractUrl(String string) {
        return extractByRegex(string, PATTERN_URL);
    }

    /**
     * Extract a String by a regex
     *
     * @param string
     * @param expressaoRegular
     * @return
     * @throws PatternSyntaxException
     */
    public static String extractByRegex(String string, String expressaoRegular) throws PatternSyntaxException {

        String novaString = "";

        try {
            Pattern pattern = Pattern.compile(expressaoRegular);

            Matcher matcher = pattern.matcher(string);
            while (matcher.find()) {
                novaString = matcher.group();
            }
        } catch (PatternSyntaxException ex) {
            Logger.getLogger(StringUtils.class.getName()).log(Level.WARNING, ex.getMessage());
        }

        return novaString;
    }

    /**
     * Convert s String to double value
     *
     * @param string
     * @return
     * @throws NumberFormatException
     */
    public static Double stringToNumber(String string) throws NumberFormatException {

        Double valor = null;

        if (string == null || string.trim().isEmpty()) {
            return null;
        }
        try {
            if (!string.contains(".") && !string.contains(",")) {
                return Double.valueOf(StringUtils.getOnlyIntegerNumbers(string));
            }

            if (string.contains(".") && !string.contains(",")) {

                String decimals = string.substring(string.lastIndexOf("."), string.length() - 1);

                if (decimals.length() == 2 || decimals.length() == 1) {
                    return Double.valueOf(string);
                } else {
                    return Double.valueOf(string.replace(".", ""));
                }
            }

            if (!string.contains(".") && string.contains(",")) {
                return Double.valueOf(string.replace(",", "."));
            }

            if (string.contains(".") && string.contains(",")) {
                if (string.indexOf(".") > string.indexOf(",")) {
                    return Double.valueOf(string.replace(",", ""));
                } else {
                    return Double.valueOf(string.replace(".", "").replace(",", "."));
                }
            }
        } catch (NumberFormatException ex) {
            throw new NumberFormatException("Número inválido: " + string);
        }

        return valor;
    }

    /**
     * Return only alpha characters
     *
     * @param string
     * @return
     */
    public static String onlyAlpha(String string) {
        return string.replaceAll("[^A-Za-zçáàãâäéèêëíìîïóòõôöúùûüÇÁÀÃÂÄÉÈÊËÍÌÎÏÓÒÕÔÖÚÙÛÜ ,.]", "");
    }

    /**
     * Convert a String to Camel Case
     *
     * @param string
     * @return
     */
    public static String converteToCamelCase(String string) {
        StringBuilder stringCamelCase = new StringBuilder();
        string = removeDuplicateWhiteSpace(string);

        String[] quebrada = string.split(" ");
        if (quebrada.length == 0) {
            stringCamelCase.append(string.toLowerCase());
        } else {
            for (int i = 0; i < quebrada.length; i++) {
                if (i == 0) {
                    stringCamelCase.append(quebrada[i].toLowerCase());
                } else {
                    stringCamelCase.append(quebrada[i].substring(0, 1).toUpperCase());
                    stringCamelCase.append(quebrada[i].substring(1, quebrada[i].length()).toLowerCase());
                }
            }
        }
        return stringCamelCase.toString();
    }

    /**
     * Fill a number with left zeros
     *
     * @param number
     * @param size size of generated String
     * @return
     */
    public static String fillZeros(int number, int size) {
        return fillZeros(String.valueOf(number), size);
    }

    /**
     * Fill a String with left zeros
     *
     * @param string
     * @param size size of generated String
     * @return
     */
    public static String fillZeros(String string, int size) {
        String value = "";
        if (string != null && !string.trim().isEmpty()) {
            value = string;
            for (int x = value.length(); x < size; x++) {
                value = "0" + value;
            }
        }
        return value;
    }

    /**
     * return the String with first character with lower case Example: "Paul"
     * returns "paul"
     *
     * @param string
     * @return
     */
    public static String getLowerFirstLetter(String string) {
        if (string == null) {
            return null;
        }
        if (string.length() == 1) {
            return string.toLowerCase();
        }
        if (string.length() > 1) {
            return string.substring(0, 1).toLowerCase() + "" + string.substring(1, string.length());
        }
        return "";
    }

    /**
     * return the String with first character with upper case Example: "paul"
     * returns "Paul"
     *
     * @param string
     * @return
     */
    public static String getUpperFirstLetter(String string) {
        if (string == null) {
            return null;
        }
        if (string.length() == 1) {
            return string.toUpperCase();
        }
        if (string.length() > 1) {
            return string.substring(0, 1).toUpperCase() + "" + string.substring(1, string.length());
        }
        return "";
    }

    /**
     * Return true if the string only contais letters
     *
     * @param string
     * @return
     */
    public static boolean isOnlyLetter(String string) {
        char[] chars = string.toCharArray();
        for (char c : chars) {
            if (!Character.isLetter(c)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return the String with no accents
     *
     * @param value
     * @return
     */
    public static String removeAccent(String value) {
        return Conversion.removeAccent(value);
    }
}
