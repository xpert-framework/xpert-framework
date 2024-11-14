package com.xpert.faces.component.cnpj;

import com.xpert.faces.conversion.CnpjConverter;
import com.xpert.faces.validation.CnpjValidator;
import java.io.Serializable;
import org.primefaces.component.inputmask.InputMask;

/**
 * Component "cnpj". This component is a primefaces InputMask but with cnpjValidator and cnpjConverter
 *
 * @author ayslan
 */
public class Cnpj extends InputMask implements Serializable {

    private static final long serialVersionUID = 2063994041894530291L;
    private static final String MASK = "99.999.999/9999-99";

    public Cnpj() {
        setMask(MASK);
        addValidator(new CnpjValidator());
        setConverter(new CnpjConverter());
    }

    public static final String COMPONENT_FAMILY = "com.xpert.component";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

}
