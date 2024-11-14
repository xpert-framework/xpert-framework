package com.xpert.i18n;

import java.io.Serializable;
import java.util.Locale;

/**
 *
 * @author ayslan
 */
public class XpertResourceBundle implements Serializable {

    private static final long serialVersionUID = 2714531149256469172L;
    
    public static final String CORE_BUNDLE_PATH = "com.xpert.messages";

    public static String get(String key) {
        return ResourceBundleUtils.get(key, CORE_BUNDLE_PATH);
    }
    
    public static String get(String key, Locale locale) {
        return ResourceBundleUtils.get(key, CORE_BUNDLE_PATH, XpertResourceBundle.class.getClassLoader(), locale);
    }

    public static String get(String key, Locale locale, Object... array) {
        return ResourceBundleUtils.get(key, CORE_BUNDLE_PATH, XpertResourceBundle.class.getClassLoader(), locale, array);
    }

    public static String get(String key, Object... array) {
        return ResourceBundleUtils.get(key, CORE_BUNDLE_PATH, XpertResourceBundle.class.getClassLoader(), array);
    }
}
