package com.xpert.faces.bean;

import com.xpert.faces.utils.FacesUtils;
import java.io.Serializable;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;

/**
 *
 * @author ayslan
 */
public class LocaleBean implements Serializable {

    private Locale locale;
    private List<Locale> locales = new ArrayList<>();

    public LocaleBean() {
        locale = FacesContext.getCurrentInstance().getApplication().getDefaultLocale();
        if (locale == null) {
            locale = FacesContext.getCurrentInstance().getViewRoot().getLocale();
        }
        locales = new ArrayList<>();
        Iterator<Locale> it = FacesContext.getCurrentInstance().getApplication().getSupportedLocales();
        while (it.hasNext()) {
            locales.add(it.next());
        }
    }

    public String changeLocale() {
        HttpServletRequest request = FacesUtils.getRequest();
        String view = request.getRequestURI().replaceFirst(request.getContextPath(), "");
        String queryString = FacesUtils.getRequest().getQueryString();
        return view + "?" + (queryString == null ? "" : queryString + "&") + "faces-redirect=true";
    }

    public DecimalFormatSymbols getDecimalFormatSymbols() {
        return new DecimalFormatSymbols(FacesContext.getCurrentInstance().getViewRoot().getLocale());
    }

    public char getDecimalSeparator() {
        return getDecimalFormatSymbols().getDecimalSeparator();
    }

    public char getGroupingSeparator() {
        return getDecimalFormatSymbols().getGroupingSeparator();
    }

    public String getCurrencySymbol() {
        return getDecimalFormatSymbols().getCurrencySymbol();
    }

    public List<Locale> getLocales() {
        return locales;
    }

    public void setLocales(List<Locale> locales) {
        this.locales = locales;
    }

    public Locale getLocale() {
        return locale;
    }

    public void setLocale(Locale locale) {
        this.locale = locale;
    }
}
