package com.xpert.core.validation;

import com.xpert.core.conversion.NumberUtils;
import com.xpert.faces.utils.ValueExpressionAnalyzer;
import com.xpert.i18n.CustomInterpolator;
import com.xpert.i18n.I18N;
import com.xpert.i18n.XpertResourceBundle;
import com.xpert.utils.StringUtils;
import jakarta.el.ValueExpression;
import jakarta.el.ValueReference;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.component.UIComponent;
import jakarta.faces.context.FacesContext;
import jakarta.faces.validator.ValidatorException;
import jakarta.validation.ValidatorFactory;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Size;
import org.hibernate.validator.constraints.Email;
import org.hibernate.validator.constraints.NotBlank;
import org.hibernate.validator.constraints.NotEmpty;
import org.hibernate.validator.constraints.Range;
import org.hibernate.validator.constraints.URL;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author ayslan
 */
public class BeanValidator extends jakarta.faces.validator.BeanValidator {

    private static final boolean DEBUG = false;
    private static final Logger logger = Logger.getLogger(BeanValidator.class.getName());
    private static final List<Class> VALIDATION_TYPES = new ArrayList<>();

    private static final String CPF_CONSTRAINT_CLASS = "org.hibernate.validator.constraints.br.CPF";
    private static final String CNPJ_CONSTRAINT_CLASS = "org.hibernate.validator.constraints.br.CNPJ";
    private static final String TITULO_ELEITORAL_CONSTRAINT_CLASS = "org.hibernate.validator.constraints.br.TituloEleitoral";

    static {
        VALIDATION_TYPES.add(NotNull.class);
        VALIDATION_TYPES.add(NotBlank.class);
        VALIDATION_TYPES.add(NotEmpty.class);
        VALIDATION_TYPES.add(Max.class);
        VALIDATION_TYPES.add(Min.class);
        VALIDATION_TYPES.add(Size.class);
        VALIDATION_TYPES.add(Range.class);
        VALIDATION_TYPES.add(Email.class);
        VALIDATION_TYPES.add(DecimalMax.class);
        VALIDATION_TYPES.add(DecimalMin.class);
        VALIDATION_TYPES.add(URL.class);
        VALIDATION_TYPES.add(Past.class);
        VALIDATION_TYPES.add(Future.class);

        try {
            VALIDATION_TYPES.add(Class.forName(CPF_CONSTRAINT_CLASS));
            VALIDATION_TYPES.add(Class.forName(CNPJ_CONSTRAINT_CLASS));
            VALIDATION_TYPES.add(Class.forName(TITULO_ELEITORAL_CONSTRAINT_CLASS));
        } catch (ClassNotFoundException ex) {
            //nothing
        }

    }

    @Override
    public void validate(FacesContext context, UIComponent component, Object value) {

        Object cachedObject = context.getExternalContext().getApplicationMap().get(VALIDATOR_FACTORY_KEY);
        if (cachedObject instanceof ValidatorFactory) {
            ValidatorFactory validatorFactory = (ValidatorFactory) cachedObject;
            //if not instance of CustomInterpolator, then force
            //weblogic for some reason doesnt set the interpolator from validation.xml
            if (validatorFactory.getMessageInterpolator() instanceof CustomInterpolator == false) {
                validatorFactory = jakarta.validation.Validation.byDefaultProvider()
                        .configure()
                        .messageInterpolator(new CustomInterpolator())
                        .buildValidatorFactory();
                context.getExternalContext().getApplicationMap().put(VALIDATOR_FACTORY_KEY, validatorFactory);
            }
        }

        try {
            super.validate(context, component, value);
        } catch (ValidatorException ex) {

            ValueReference valueReference = getValueReference(context, component);
            List<FacesMessage> messages = new ArrayList<>();

            if (ex.getFacesMessages() == null || !ex.getFacesMessages().contains(ex.getFacesMessage())) {
                messages.add(ex.getFacesMessage());
            }

            if (ex.getFacesMessages() != null) {
                messages.addAll(ex.getFacesMessages());
            }

            if (DEBUG) {
                logger.log(Level.INFO, "Caught a ValidatorException. {0} messages.", messages.size());
            }

            for (FacesMessage facesMessage : messages) {
                if (DEBUG) {
                    logger.log(Level.INFO, "Message summary: {0}", facesMessage.getSummary());
                }
                String message = getMessage(facesMessage.getSummary(), valueReference);
                if (DEBUG) {
                    logger.log(Level.INFO, "Message resolved: {0}", message);
                }
                facesMessage.setSummary(message);
                facesMessage.setDetail(message);
            }

            throw new ValidatorException(messages);
        }

    }

    public ValueReference getValueReference(FacesContext context, UIComponent component) {

        ValueExpression valueExpression = component.getValueExpression("value");
        ValueExpressionAnalyzer expressionAnalyzer = new ValueExpressionAnalyzer(valueExpression);

        return expressionAnalyzer.getReference(context.getELContext());
    }

    /**
     * The messages are formated like:
     *
     * {org.hibernate.validator.constraints.NotBlank.message}
     * {jakarta.validation.constraints.NotNull.message}
     *
     *
     * @param message
     * @return
     */
    private Class getViolation(String message) {
        for (Class clazz : VALIDATION_TYPES) {
            if (message.contains(clazz.getName())) {
                return clazz;
            }
        }
        return null;
    }

    public String getMessage(String message, ValueReference valueReference) {
        Class violation = getViolation(message);
        if (violation != null) {
            if (DEBUG) {
                logger.log(Level.INFO, "Violation: {0}", violation.getName());
            }
            String object = getAttributeName(valueReference, valueReference.getBase().getClass());

            if (violation.equals(Email.class)) {
                return XpertResourceBundle.get("invalidEmail");
            }

            if (violation.equals(URL.class)) {
                return XpertResourceBundle.get("invalidURL");
            }

            if (violation.equals(NotNull.class) || violation.equals(NotEmpty.class) || violation.equals(NotBlank.class)) {
                return object + " " + XpertResourceBundle.get("isRequired");
            }
            if (violation.equals(Past.class)) {
                return object + " " + XpertResourceBundle.get("mustBeAPastDate");
            }
            if (violation.equals(Future.class)) {
                return object + " " + XpertResourceBundle.get("mustBeAFutureDate");
            }
            if (violation.getName().equals(CPF_CONSTRAINT_CLASS)) {
                return XpertResourceBundle.get("invalidCpf");
            }
            if (violation.getName().equals(CNPJ_CONSTRAINT_CLASS)) {
                return XpertResourceBundle.get("invalidCnpj");
            }
            if (violation.getName().equals(TITULO_ELEITORAL_CONSTRAINT_CLASS)) {
                return XpertResourceBundle.get("invalidTituloEleitoral");
            }

            return getMessageWithDefinedValue(object, valueReference, violation);

        } else {
            if (DEBUG) {
                logger.log(Level.INFO, "Violation for message \"{0}\" not found", message);
            }
        }
        return message.replace("{", "").replace("}", "");
    }

    /**
     * return class from resourcebundle the message for: simple name (First
     * Letter lowercase) + "." + property
     *
     * Ex: Class Person and attribute name - person.name
     *
     * @param valueReference
     * @param clazz
     * @return
     */
    public String getAttributeName(ValueReference valueReference, Class clazz) {
        return I18N.getAttributeName(clazz, valueReference.getProperty().toString());
    }

    /**
     *
     * @Size id for String or Collections/Map, the message is diferent from the
     * two cases
     * @Max and
     * @Min is for Numbers (Long, BigInteger, etc...)
     *
     * @param object
     * @param valueReference
     * @param violation
     * @return
     */
    public String getMessageWithDefinedValue(String object, ValueReference valueReference, Class violation) {

        AnnotationFromViolation annotationFromViolation = getAnnotation(valueReference, violation);
        Annotation annotation = annotationFromViolation.getAnnotation();

        if (violation.equals(Max.class)) {
            Long max = ((Max) annotation).value();
            return object + " " + XpertResourceBundle.get("numberMax", max);
        }
        if (violation.equals(Min.class)) {
            Long min = ((Min) annotation).value();
            return object + " " + XpertResourceBundle.get("numberMin", min);
        }
        if (violation.equals(DecimalMax.class)) {
            String max = ((DecimalMax) annotation).value();
            return object + " " + XpertResourceBundle.get("numberMax", NumberUtils.convertToNumber(max));
        }
        if (violation.equals(DecimalMin.class)) {
            String min = ((DecimalMin) annotation).value();
            return object + " " + XpertResourceBundle.get("numberMin", NumberUtils.convertToNumber(min));
        }

        if (violation.equals(Size.class)) {

            Integer min = ((Size) annotation).min();
            Integer max = ((Size) annotation).max();

            if (min != null && max != null && min > 0 && max < Integer.MAX_VALUE) {
                if (annotationFromViolation.isForChar()) {
                    return object + " " + XpertResourceBundle.get("charMaxMin", min, max);
                } else {
                    return object + " " + XpertResourceBundle.get("sizeMaxMin", max);
                }
            }
            if (min != null && min > 0) {
                if (annotationFromViolation.isForChar()) {
                    return object + " " + XpertResourceBundle.get("charMin", min);
                } else {
                    return object + " " + XpertResourceBundle.get("sizeMin", min);
                }
            }
            if (max != null && max > 0) {
                if (annotationFromViolation.isForChar()) {
                    return object + " " + XpertResourceBundle.get("charMax", max);
                } else {
                    return object + " " + XpertResourceBundle.get("sizeMax", max);
                }
            }
        }

        return "";
    }

    public AnnotationFromViolation getAnnotation(ValueReference valueReference, Class violation) {
        try {

            Field field = getDeclaredField(valueReference.getBase().getClass(), valueReference.getProperty().toString());

            if (field != null) {
                return new AnnotationFromViolation(field.getAnnotation(violation), isChar(field));
            }

            Method method = getDeclaredMethod(valueReference.getBase().getClass(), valueReference.getProperty().toString());
            if (method != null) {
                return new AnnotationFromViolation(method.getAnnotation(violation), isChar(method));
            }

            return null;
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Field getDeclaredField(Class clazz, String fieldName) {
        Field field = null;

        try {
            field = clazz.getDeclaredField(fieldName);
        } catch (NoSuchFieldException ex) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
                return getDeclaredField(clazz.getSuperclass(), fieldName);
            }
        } catch (SecurityException ex) {
            logger.log(Level.INFO, null, ex);
        }

        return field;
    }

    public Method getDeclaredMethod(Class clazz, String fieldName) {
        Method method = null;

        try {
            method = clazz.getDeclaredMethod("get" + StringUtils.getUpperFirstLetter(fieldName));
        } catch (NoSuchMethodException ex) {
            if (clazz.getSuperclass() != null && !clazz.getSuperclass().equals(Object.class)) {
                return getDeclaredMethod(clazz.getSuperclass(), fieldName);
            }
        } catch (SecurityException ex) {
            logger.log(Level.INFO, null, ex);
        }

        return method;
    }

    public boolean isChar(Field field) {
        if (field.getType().equals(String.class) || field.getType().equals(CharSequence.class)) {
            return true;
        }
        return false;
    }

    public boolean isChar(Method method) {
        if (method.getReturnType().equals(String.class) || method.getReturnType().equals(CharSequence.class)) {
            return true;
        }
        return false;
    }

    public class AnnotationFromViolation {

        private Annotation annotation;
        private boolean forChar;

        public AnnotationFromViolation(Annotation annotation, boolean forChar) {
            this.annotation = annotation;
            this.forChar = forChar;
        }

        public Annotation getAnnotation() {
            return annotation;
        }

        public void setAnnotation(Annotation annotation) {
            this.annotation = annotation;
        }

        public boolean isForChar() {
            return forChar;
        }

        public void setForChar(boolean forChar) {
            this.forChar = forChar;
        }
    }
}
