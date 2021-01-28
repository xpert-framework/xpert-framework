package com.xpert.core.exception;

import com.xpert.i18n.I18N;
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
     *
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
     * throws a StackException if getExceptions() is not empty
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
            exceptions = new ArrayList<>();
        }
        exceptions.add(new StackException(mensagem, parametros));
    }

    public void add(StackException ex) {
        if (exceptions == null) {
            exceptions = new ArrayList<>();
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
     * Return messages from StackException. String returned is Array of
     * getMessage() and getMessage() from each getExceptions()
     *
     * @return
     */
    public String[] getMessages() {
        return getMessagesList().toArray(new String[0]);
    }

    /**
     * Return messages from StackException. String returned is Array of
     * getMessage() and getMessage() from each getExceptions()
     *
     * @param i18n if true then user I18N class to resolve messages
     * @return
     */
    public String[] getMessages(boolean i18n) {
        return getMessagesList(i18n).toArray(new String[0]);
    }

    /**
     * Return messages from StackException. String returned is List of
     * getMessage() and getMessage() from each getExceptions()
     *
     * @return
     */
    public List<String> getMessagesList() {

        List<String> messages = new ArrayList<>();
        if (getMessage() != null) {
            messages.add(getMessage());
        }
        if (getExceptions() != null) {
            for (StackException se : getExceptions()) {
                if (se.getMessage() != null) {
                    messages.add(se.getMessage());
                }
            }
        }
        return messages;
    }

    /**
     * Return messages from StackException. String returned is List of
     * getMessage() and getMessage() from each getExceptions()
     *
     * @param i18n if true then user I18N class to resolve messages
     * @return
     */
    public List<String> getMessagesList(boolean i18n) {

        List<String> messages = new ArrayList<>();
        if (i18n == false) {
            return getMessagesList();
        }

        if (getMessage() != null) {
            messages.add(I18N.get(getMessage()));
        }
        if (getExceptions() != null) {
            for (StackException se : getExceptions()) {
                if (se.getMessage() != null) {
                    messages.add(I18N.get(se.getMessage()));
                }
            }
        }
        return messages;
    }

    /**
     * Return messages from StackException. String returned is as concat of
     * getMessage() and getMessage() from each getExceptions()
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
                if (se.getMessage() != null) {
                    stackMessage.append(se.getMessage()).append("\n");
                }
            }
        }
        return stackMessage.toString();
    }

    /**
     * Return messages from StackException. String returned is as concat of
     * getMessage() and getMessage() from each getExceptions()
     *
     * @param i18n if true then user I18N class to resolve messages
     * @return
     */
    public String getStackMessage(boolean i18n) {

        if (i18n == false) {
            return getStackMessage();
        }

        StringBuilder stackMessage = new StringBuilder();
        if (getMessage() != null) {
            stackMessage.append(I18N.get(getMessage())).append("\n");
        }
        if (getExceptions() != null) {
            for (StackException se : getExceptions()) {
                if (se.getMessage() != null) {
                    stackMessage.append(I18N.get(se.getMessage())).append("\n");
                }
            }
        }
        return stackMessage.toString();
    }
}
