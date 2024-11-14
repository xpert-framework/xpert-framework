package com.xpert.persistence.exception;

/**
 *
 * @author ayslan
 */
public class QueryFileNotFoundException extends RuntimeException {

    private static final long serialVersionUID = -6375975738898119948L;

    public QueryFileNotFoundException(String message) {
        super(message);
    }
    
}
