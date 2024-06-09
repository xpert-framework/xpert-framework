package com.xpert.core.exception;

import jakarta.ejb.ApplicationException;
import java.io.Serializable;

@ApplicationException(rollback = true)
public class BusinessException extends StackException implements Serializable {

    private static final long serialVersionUID = 5373635135051321032L;

    public BusinessException() {
    }

    public BusinessException(String mensagem, Object... parametros) {
        super(mensagem, parametros);
    }

    public BusinessException(String mensagem) {
        super(mensagem);
    }

    /**
     * throws a BusinessException if getExceptions() is not empty
     *
     * @throws BusinessException
     */
    @Override
    public void check() throws BusinessException {
        if (this.isNotEmpty()) {
            throw this;
        }
    }
}
