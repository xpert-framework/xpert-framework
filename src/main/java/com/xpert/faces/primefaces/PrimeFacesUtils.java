package com.xpert.faces.primefaces;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.primefaces.PrimeFaces;
import org.primefaces.context.PrimeRequestContext;

/**
 * Util class to Primefaces
 *
 * @author ayslan
 */
public class PrimeFacesUtils implements Serializable {

    private static final long serialVersionUID = -3199960848303235681L;

    public static String normalizeWidgetVar(String dialog) {
        return "PF('" + dialog + "')";
    }

    public static void closeDialog(String dialog) {
        if (dialog != null && !dialog.trim().isEmpty()) {
            dialog = normalizeWidgetVar(dialog);
            executeScript(dialog + ".hide()");
        }
    }

    public static void showDialog(String dialog) {
        if (dialog != null && !dialog.trim().isEmpty()) {
            dialog = normalizeWidgetVar(dialog);
            executeScript(dialog + ".show()");
        }
    }

    public static void update(String... targets) {

        if (isUseRequestContext()) {
            for (String string : targets) {
                invokeMethodRequestContext("update", String.class, string);
            }
        } else {
            PrimeFaces.current().ajax().update(targets);
        }
    }

    /**
     * Returns the legacy
     * org.primefaces.context.RequestContext.getCurrentInstance() (used in
     * primefaces 4,5 and 6)
     *
     * @return
     * @throws IllegalAccessException
     * @throws IllegalArgumentException
     * @throws InvocationTargetException
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     */
    private static Object getRequestContextInstance() throws IllegalAccessException, IllegalArgumentException, InvocationTargetException, ClassNotFoundException, NoSuchMethodException {
        //Simulate RequestContext.getCurrentInstance().execute() or RequestContext.getCurrentInstance().update(String...)
        Class requestContextClass = Class.forName("org.primefaces.context.RequestContext");
        //acess getCurrentInstance()
        Method methodGetCurrentInstance = requestContextClass.getDeclaredMethod("getCurrentInstance");
        return methodGetCurrentInstance.invoke(null);
    }

    private static void invokeMethodRequestContext(String methodName, Class methodSignature, Object arguments) {
        try {
            Object requestContext = getRequestContextInstance();
            Method methodExecute = requestContext.getClass().getDeclaredMethod(methodName, methodSignature);
            methodExecute.invoke(requestContext, arguments);
        } catch (SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException | NoSuchMethodException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void executeScript(String script) {
        if (isUseRequestContext()) {
            invokeMethodRequestContext("execute", String.class, script);
        } else {
            PrimeFaces.current().executeScript(script);
        }
    }

    /*
    Primefaces versions greater or equeals 6.x deprecated RequestContext;
     */
    private static Boolean USE_REQUEST_CONTEXT;

    /**
     * For lagacy primafaces the correct use is:
     * RequestContext.getRequestContext(); from 6 is Primefaces.current()
     *
     * @return
     */
    public static boolean isUseRequestContext() {
        if (USE_REQUEST_CONTEXT != null) {
            return USE_REQUEST_CONTEXT;
        } else {
            try {
                Class.forName("org.primefaces.context.RequestContext");
                USE_REQUEST_CONTEXT = true;
            } catch (ClassNotFoundException ex) {
                USE_REQUEST_CONTEXT = false;
            }

        }
        return USE_REQUEST_CONTEXT;

    }

    /**
     * This method get by reflection primefaces build version.
     *
     * The reason why use reflection is:
     *
     * Primefaces 3 uses org.primefaces.util.Constants.
     *
     * Primefaces 4,5, and 6 uses
     * RequestContext.getCurrentInstance().getApplicationContext().getConfig().getBuildVersion().
     *
     * However in primefaces 4 and 5 getConfig() returns
     * org.primefaces.config.ConfigConteiner and primefaces 6 returns
     * org.primefaces.config.PrimeConfiguration.
     *
     *
     * @return
     */
    public static String getPrimefacesBuildVersion() {

        if (isUseRequestContext()) {

            try {
                Object requestContext = getRequestContextInstance();
                //acess getApplicationContext()
                Method methodGetApplicationContext= requestContext.getClass().getDeclaredMethod("getApplicationContext");
                Object applicationContext = methodGetApplicationContext.invoke(requestContext);
                //acess getConfig()
                Method methodGetConfig = applicationContext.getClass().getDeclaredMethod("getConfig");
                Object config = methodGetConfig.invoke(applicationContext);
                //acess getBuildVersion()
                Method methodGetBuildVersion = config.getClass().getDeclaredMethod("getBuildVersion");
                String buildVersion = (String) methodGetBuildVersion.invoke(config);

                return buildVersion;
            } catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | ClassNotFoundException ex) {
                throw new RuntimeException(ex);
            }
        } else {
            //for primefaces > 6
            return PrimeRequestContext.getCurrentInstance().getApplicationContext().getEnvironment().getBuildVersion();
        }
    }

}
