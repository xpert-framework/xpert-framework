package com.xpert.core.exception;

import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author ayslan
 */
public class StackException extends Exception {

    private Object[] parameters;
    private List<StackException> exceptions;

    public StackException(String mensagem) {
        super(mensagem);
    }

    public StackException(String mensagem, Object... parameters) {
        super(mensagem);
        this.parameters = parameters;
    }
    

    public StackException() {
    }

    /**
     * same as "getParameters"
     * @return 
     */
    public Object[] getParametros() {
        return parameters;
    }

    public Object[] getParameters() {
        return parameters;
    }

    public List<StackException> getExceptions() {
        return exceptions;
    }

    public void setExceptions(List<StackException> excecoes) {
        this.exceptions = excecoes;
    }

    /**
     * throws a StackException  if getExceptions() is not empty
     * 
     * @throws StackException 
     */
    public void check() throws StackException {
        if (this.isNotEmpty()) {
            throw this;
        }
    }

    public void add(String mensagem, Object... parametros) {
        if (exceptions == null) {
            exceptions = new ArrayList<StackException>();
        }
        exceptions.add(new StackException(mensagem, parametros));
    }

    public void add(StackException ex) {
        if (exceptions == null) {
            exceptions = new ArrayList<StackException>();
        }
        if (ex.getExceptions() == null || ex.getExceptions().isEmpty()) {
            exceptions.add(ex);
        } else {
            for (StackException ce : ex.getExceptions()) {
                exceptions.add(ce);
            }
        }
    }

    public boolean isNotEmpty() {
        return exceptions != null && !exceptions.isEmpty();
    }

    public void clear() {
        if (exceptions != null && !exceptions.isEmpty()) {
            exceptions.clear();
        }
    }

    /**
     * Return messages from StackException. String returned is as concat of getMessage() and getMessage() from each getExceptions()
     * 
     * @return 
     */
    public String getStackMessage() {
        StringBuilder stackMessage = new StringBuilder();
        if (getMessage() != null) {
            stackMessage.append(getMessage()).append("\n");
        }
        if (getExceptions() != null) {
            for (StackException se : getExceptions()) {
                stackMessage.append(se.getMessage()).append("\n");
            }
        }
        return stackMessage.toString();
    }
}
