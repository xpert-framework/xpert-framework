package com.xpert.faces.component.cpf;

import com.xpert.faces.conversion.CpfConverter;
import com.xpert.faces.validation.CpfValidator;
import java.io.Serializable;
import org.primefaces.component.inputmask.InputMask;

/**
 * Component "cpf". This component is a primefaces InputMask but with cpfValidator and cpfConverter

 *
 * @author ayslan
 */
public class Cpf extends InputMask implements Serializable {

    private static final long serialVersionUID = 5011947516645659472L;
    
    private static final String MASK = "999.999.999-99";

    public Cpf() {
        setMask(MASK);
        addValidator(new CpfValidator());
        setConverter(new CpfConverter());
    }

    public static final String COMPONENT_FAMILY = "com.xpert.component";

    @Override
    public String getFamily() {
        return COMPONENT_FAMILY;
    }

}
