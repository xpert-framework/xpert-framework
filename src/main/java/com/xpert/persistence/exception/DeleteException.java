package com.xpert.persistence.exception;

import jakarta.ejb.ApplicationException;

/**
 *
 * @author ayslan
 */
@ApplicationException(rollback = true)
public class DeleteException extends Exception {

    private static final long serialVersionUID = -1005188569473946574L;

    public DeleteException() {
    }

    public DeleteException(Throwable cause) {
        super(cause);
    }

    public DeleteException(String message) {
        super(message);
    }
}
