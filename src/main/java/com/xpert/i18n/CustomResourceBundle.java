package com.xpert.i18n;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.ResourceBundle;

/**
 * Customize messages in view.
 * This bundle evicts a not found property that JSF shows something like "???propertyName??"
 *
 * @author ayslan
 */
public class CustomResourceBundle extends ResourceBundle implements Serializable {

    private static final long serialVersionUID = 7975035544471795737L;

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
