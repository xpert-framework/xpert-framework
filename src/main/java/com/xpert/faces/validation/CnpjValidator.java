package com.xpert.faces.validation;

import com.xpert.core.validation.Validation;
import com.xpert.i18n.XpertResourceBundle;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.Validator;
import jakarta.faces.validator.ValidatorException;
import java.io.Serializable;

public class CnpjValidator implements Validator, Serializable {

    private static final long serialVersionUID = -1781746398966326747L;

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) throws ValidatorException {

        if (value != null && !value.toString().trim().isEmpty()) {
            if (!Validation.validateCNPJ(value.toString())) {
                FacesMessage msg = new FacesMessage(XpertResourceBundle.get("invalidCnpj"));
                msg.setSeverity(FacesMessage.SEVERITY_ERROR);
                throw new ValidatorException(msg);
            }
        }
    }
}
