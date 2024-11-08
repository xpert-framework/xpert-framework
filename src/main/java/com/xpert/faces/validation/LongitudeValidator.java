package com.xpert.faces.validation;

import com.xpert.core.validation.Validation;
import com.xpert.i18n.XpertResourceBundle;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;

public class LongitudeValidator implements Validator {

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {
        if (value != null && !value.toString().trim().isEmpty()) {
            if (!Validation.validateLongitude(value.toString())) {
                FacesMessage msg = new FacesMessage(XpertResourceBundle.get("invalidLongitude"));
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }

    }
}
