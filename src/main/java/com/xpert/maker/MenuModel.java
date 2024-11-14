package com.xpert.maker;

import java.io.Serializable;

/**
 *
 * @author ayslan
 */
public class MenuModel implements Serializable {

    private static final long serialVersionUID = -4983816238882410695L;

    private String label;
    private String url;

    public MenuModel(String label, String url) {
        this.label = label;
        this.url = url;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }
}
