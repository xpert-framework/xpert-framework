package com.xpert.faces.component.inputnumber;

import jakarta.faces.application.ResourceDependencies;
import jakarta.faces.application.ResourceDependency;
import jakarta.faces.convert.NumberConverter;
import org.primefaces.component.inputtext.InputText;

/**
 * Component "inputNumber". This component is a primefaces InputText but with
 * some "number" funcionalities
 *
 * @author ayslan
 */
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "xpert", name = "scripts/core.js"),
    @ResourceDependency(library = "xpert", name = "scripts/jquery.price_format.js")
})
public class InputNumber extends InputText {

    public static final String COMPONENT_FAMILY = "com.xpert.component";
    public static final int DEFAULT_LIMIT = 15;
    public static final int DEFAULT_CENTS_LIMIT = 2;

    public InputNumber() {
        NumberConverter numberConverter = new NumberConverter();
        Integer centLimit =(Integer) getStateHelper().eval(PropertyKeys.centsLimit, DEFAULT_CENTS_LIMIT);
        numberConverter.setMaxFractionDigits(centLimit);
        numberConverter.setMinFractionDigits(centLimit);
        setConverter(numberConverter);
    }
    
    protected enum PropertyKeys {

        limit, centsSeparator, thousandsSeparator, centsLimit, allowNegative;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    public Boolean getAllowNegative() {
        return (Boolean) getStateHelper().eval(PropertyKeys.allowNegative, false);
    }

    public void setAllowNegative(Boolean allowNegative) {
        getStateHelper().put(PropertyKeys.allowNegative, allowNegative);
    }

    public Integer getLimit() {
        return (Integer) getStateHelper().eval(PropertyKeys.limit, DEFAULT_LIMIT);
    }

    public void setLimit(Integer limit) {
        getStateHelper().put(PropertyKeys.limit, limit);
    }

    public String getCentsSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.centsSeparator, null);
    }

    public void setCentsSeparator(String centsSeparator) {
        getStateHelper().put(PropertyKeys.centsSeparator, centsSeparator);
    }

    public String getThousandsSeparator() {
        return (String) getStateHelper().eval(PropertyKeys.thousandsSeparator, null);
    }

    public void setThousandsSeparator(String thousandsSeparator) {
        getStateHelper().put(PropertyKeys.thousandsSeparator, thousandsSeparator);
    }

    public Integer getCentsLimit() {
        return (Integer) getStateHelper().eval(PropertyKeys.centsLimit, DEFAULT_CENTS_LIMIT);
    }

    public void setCentsLimit(Integer centsLimit) {
        getStateHelper().put(PropertyKeys.centsLimit, centsLimit);
    }

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

}
