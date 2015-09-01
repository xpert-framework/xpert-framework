package com.xpert.i18n;

import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Customize messages in view.
 * This bundle evicts a not found property that JSF shows something like "???propertyName??"
 *
 * @author Ayslan
 */
public class CustomResourceBundle extends ResourceBundle {

    public CustomResourceBundle() {
    }

    @Override
    public Enumeration<String> getKeys() {
        return parent.getKeys();
    }

    @Override
    protected Object handleGetObject(String key) {
        return I18N.get(key);
    }
}
