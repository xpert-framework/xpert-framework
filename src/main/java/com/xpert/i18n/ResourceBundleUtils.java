package com.xpert.i18n;

import com.xpert.core.conversion.NumberUtils;
import com.xpert.utils.DateUtils;
import com.xpert.utils.StringUtils;
import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author ayslan
 */
public class ResourceBundleUtils {

    private static final Logger logger = Logger.getLogger(ResourceBundleUtils.class.getName());
    public static final Locale PT_BR = new Locale("pt", "BR");
    private static final Pattern PATTERN_FIND_NUMBER = Pattern.compile("\\{[0-9]\\}");

    /**
     *
     * Get the message and format like passed parameters.
     * Parameters must be in format "{0} {1}".
     * Example: There is a {0} in object {1}
     *
     * @param key
     * @param bundle
     * @param array
     * @return
     */
    public static String get(String key, String bundle, Object... array) {
        return get(key, bundle, null, array);
    }

    public static String get(String key, String bundle, ClassLoader classLoader, Object... array) {
        return get(key, bundle, classLoader, I18N.getLocale(), array);
    }

    /**
     *
     * Método que pega a mensagem e formata através dos parametros informados.
     * Os parametros devem está nas mensagens no formato: {0} {1}. Ex: Já existe
     * o município {0} cadastrado para o estado {1}
     *
     * @param key
     * @param bundle
     * @param classLoader
     * @param locale
     * @param array
     * @return
     */
    public static String get(String key, String bundle, ClassLoader classLoader, Locale locale, Object... array) {

        if (key == null || key.isEmpty()) {
            try {
                throw new IllegalArgumentException("ResourceBundle key is required");
            } catch (IllegalArgumentException ex) {
                logger.log(Level.WARNING, ex.getMessage(), ex);
            }
            return "ResourceBundle key is required";
        }

        ResourceBundle resourceBundle = null;

        try {
            if (classLoader != null) {
                resourceBundle = ResourceBundle.getBundle(bundle, locale, classLoader);
            } else {
                resourceBundle = ResourceBundle.getBundle(bundle, locale);
            }

            if (resourceBundle == null || (!locale.equals(PT_BR) && !resourceBundle.containsKey(key))) {
                resourceBundle = ResourceBundle.getBundle(bundle, PT_BR, classLoader);
            }

            if (resourceBundle == null || !resourceBundle.containsKey(key)) {
                return key;
            }

            key = resourceBundle.getString(key);

            if (array != null && array.length > 0) {
                Matcher matcher = PATTERN_FIND_NUMBER.matcher(key);
                while (matcher.find()) {
                    String chave = matcher.group();
                    int posicao = Integer.valueOf(StringUtils.getOnlyIntegerNumbers(chave));
                    if (posicao < array.length && array[posicao] != null) {
                        Object object = array[posicao];
                        key = key.replace(chave, getObjectString(object));
                    }
                }
                return key;
            }

        } catch (MissingResourceException ex2) {
            return key;
        }

        return key;
    }
    
    public static String getObjectString(Object object){
        if(object== null){
            return "";
        }
        if(object instanceof String){
            return (String)object;
        }
        if(object instanceof BigDecimal){
            return NumberUtils.convertToNumber((BigDecimal)object);
        }
        if(object instanceof Double){
            return NumberUtils.convertToNumber((Double)object);
        }
        if(object instanceof Date){
            return DateUtils.dateToString((Date)object, I18N.getDatePattern());
        }
        if(object instanceof Calendar){
            return DateUtils.dateToString((Calendar)object, I18N.getDatePattern());
        }
        return object.toString();
    }
}
