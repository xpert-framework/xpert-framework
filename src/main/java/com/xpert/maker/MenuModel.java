package com.xpert.maker;

/**
 *
 * @author ayslan
 */
public class MenuModel {

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
