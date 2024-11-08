package com.xpert.faces.component.restriction;

import com.xpert.persistence.query.LikeType;
import com.xpert.persistence.query.RestrictionType;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import jakarta.el.ELException;
import jakarta.el.ValueExpression;
import jakarta.faces.FacesException;
import jakarta.faces.component.UIComponent;
import jakarta.faces.view.facelets.ComponentHandler;
import jakarta.faces.view.facelets.FaceletContext;
import jakarta.faces.view.facelets.FaceletException;
import jakarta.faces.view.facelets.TagAttribute;
import jakarta.faces.view.facelets.TagConfig;
import jakarta.faces.view.facelets.TagHandler;
import jakarta.persistence.TemporalType;

/**
 * TagHandler for the component "x:restriction".
 * This taghandler store input values into request map. These values are used to be added into a list of restrictions or a LazyDataModel
 *
 * @author ayslan
 */
public class RestrictionTagHandler extends TagHandler {

    private final TagAttribute addTo;
    private final TagAttribute property;
    private final TagAttribute type;
    private final TagAttribute ilike;
    private final TagAttribute likeType;
    private final TagAttribute temporalType;
    private final TagAttribute rendered;
    private final TagAttribute castAs;

    public RestrictionTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        this.addTo = getAttribute("addTo");
        this.property = getAttribute("property");
        this.type = getAttribute("type");
        this.ilike = getAttribute("ilike");
        this.likeType = getAttribute("likeType");
        this.temporalType = getAttribute("temporalType");
        this.rendered = getAttribute("rendered");
        this.castAs = getAttribute("castAs");
    }

    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (ComponentHandler.isNew(parent)) {

            //add restriction to request map
            ValueExpression addToVE = null;
            ValueExpression propertyVE = null;
            ValueExpression typeVE = null;
            ValueExpression ilikeVE = null;
            ValueExpression likeTypeVE = null;
            ValueExpression temporalTypeVE = null;
            ValueExpression renderedVE = null;
            ValueExpression castAsVE = null;

            //get value expressions
            if (addTo != null) {
                addToVE = addTo.getValueExpression(faceletContext, Object.class);
            }
            
            if (castAs != null) {
                castAsVE = castAs.getValueExpression(faceletContext, Object.class);
            }

            if (property != null) {
                propertyVE = property.getValueExpression(faceletContext, String.class);
            }

            if (type != null) {
                typeVE = type.getValueExpression(faceletContext, String.class);
                //validate
                String typeString = type.getValue(faceletContext);
                //if a type is informed, then validate the type
                if (typeString != null && !typeString.trim().isEmpty()) {
                    RestrictionType restrictionType = RestrictionType.getByAcronym(typeString);
                    if (restrictionType == null) {
                        throw new FacesException("Restriction type \"" + typeString + "\" not found. The supported types are: " + RestrictionType.getAcronymList());
                    }

                }
            }

            if (ilike != null) {
                ilikeVE = ilike.getValueExpression(faceletContext, Boolean.class);
            }

            if (likeType != null) {
                likeTypeVE = likeType.getValueExpression(faceletContext, String.class);

                //validate
                String typeString = likeType.getValue(faceletContext);
                //if a type is informed, then validate the type
                if (typeString != null) {
                    boolean found = false;
                    for (LikeType lt : LikeType.values()) {
                        if (lt.name().toLowerCase().equals(typeString)) {
                            found = true;
                        }
                    }
                    if (found == false) {
                        throw new FacesException("Like type \"" + typeString + "\" not found. The supported types are: " + LikeType.getNameList());
                    }

                }
            }

            if (temporalType != null) {
                temporalTypeVE = temporalType.getValueExpression(faceletContext, String.class);

                //validate
                String typeString = temporalType.getValue(faceletContext);
                //if a type is informed, then validate the type
                if (typeString != null) {
                    boolean found = false;
                    for (TemporalType tt : TemporalType.values()) {
                        if (tt.name().toLowerCase().equals(typeString)) {
                            found = true;
                        }
                    }
                    if (found == false) {
                        throw new FacesException("Restriction type \"" + typeString + "\" not found. The supported types are: time, timestamp, date");
                    }

                }
            }
            
            if(rendered != null){
                renderedVE = rendered.getValueExpression(faceletContext, Boolean.class);
            }

            //create component
            RestrictionComponent restrictionComponent = new RestrictionComponent(parent, addToVE, propertyVE, typeVE, ilikeVE, likeTypeVE, temporalTypeVE, renderedVE, castAsVE);

            Map<String, Object> requestMap = faceletContext.getFacesContext().getExternalContext().getRequestMap();
            List<RestrictionComponent> currentRestrictions = (List<RestrictionComponent>) requestMap.get(RestrictionCollector.RESTRICTIONS);
            if (currentRestrictions == null) {
                currentRestrictions = new ArrayList<>();
            }
            currentRestrictions.add(restrictionComponent);
            //add to requestmap
            requestMap.put(RestrictionCollector.RESTRICTIONS, currentRestrictions);

        }
    }

}
