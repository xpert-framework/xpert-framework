package com.xpert.core.exception;

/**
 *
 * @author ayslan
 */
public class UniqueFieldException extends BusinessException {

    private static final long serialVersionUID = 6126286236361546543L;

    private boolean i18n = true;

    public boolean isI18n() {
        return i18n;
    }

    public void setI18n(boolean i18n) {
        this.i18n = i18n;
    }

    @Override
    public void check() throws UniqueFieldException {
        if (this.isNotEmpty()) {
            throw this;
        }
    }
}
