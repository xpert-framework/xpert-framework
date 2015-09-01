package com.xpert.faces.bean;

import com.xpert.persistence.query.Restriction;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Ayslan
 */
public class ClassModel {
    
    private String order;
    private String itemLabel;
    private List<Restriction> restrictions;
    
    public ClassModel() {
    }
    
    public ClassModel(String order) {
        this.order = order;
    }
    
    public ClassModel(String itemLabel, String order) {
        this.itemLabel = itemLabel;
        this.order = order;
    }
    
    public ClassModel(String itemLabel, List<Restriction> restrictions, String order) {
        this.order = order;
        this.restrictions = restrictions;
        this.itemLabel = itemLabel;
    }
    
    public ClassModel(String itemLabel, Restriction restriction, String order) {
        this.order = order;
        this.restrictions = new ArrayList<Restriction>();
        this.restrictions.add(restriction);
        this.itemLabel = itemLabel;
    }
    
    public String getItemLabel() {
        return itemLabel;
    }
    
    public void setItemLabel(String itemLabel) {
        this.itemLabel = itemLabel;
    }
    
    public String getOrder() {
        return order;
    }
    
    public void setOrder(String order) {
        this.order = order;
    }
    
    public List<Restriction> getRestrictions() {
        return restrictions;
    }
    
    public void setRestrictions(List<Restriction> restrictions) {
        this.restrictions = restrictions;
    }
}
