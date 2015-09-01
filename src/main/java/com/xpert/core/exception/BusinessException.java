package com.xpert.core.exception;

public class BusinessException extends StackException {

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
