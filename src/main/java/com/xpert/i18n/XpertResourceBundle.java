package com.xpert.i18n;

import java.util.Locale;

/**
 *
 * @author Ayslan
 */
public class XpertResourceBundle {

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
