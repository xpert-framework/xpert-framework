package com.xpert.faces.primefaces;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import javax.faces.FacesException;
import org.primefaces.context.ApplicationContext;
import org.primefaces.context.RequestContext;

/**
 * Util class to Primefaces
 *
 * @author ayslan
 */
public class PrimeFacesUtils {

    /**
     * Primefaces 4 and 5 new widget var is like "PF('widgetVar')"
     *
     * @param widgetVar
     * @param primeFacesVersion
     * @return
     */
    public static String normalizeWidgetVar(String widgetVar, PrimeFacesVersion primeFacesVersion) {
        if (primeFacesVersion.isUsePFWidgetVar()) {
            return "PF('" + widgetVar + "')";
        }
        return widgetVar;
    }

    public static String normalizeWidgetVar(String dialog) {
        if (dialog != null && !PrimeFacesUtils.isVersion3()) {
            return "PF('" + dialog + "')";
        }
        return dialog;
    }

    public static void closeDialog(String dialog) {
        if (dialog != null && !dialog.trim().isEmpty()) {
            dialog = normalizeWidgetVar(dialog);
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.execute(dialog + ".hide()");
        }
    }

    public static void showDialog(String dialog) {
        if (dialog != null && !dialog.trim().isEmpty()) {
            dialog = normalizeWidgetVar(dialog);
            RequestContext requestContext = RequestContext.getCurrentInstance();
            requestContext.execute(dialog + ".show()");
        }
    }

    public static void update(String... targets) {
        RequestContext context = RequestContext.getCurrentInstance();
        if (context != null) {
            for (String string : targets) {
                context.update(string);
            }
        }
    }
    private static Boolean IS_VERSION_3;

    public static boolean isVersion4() {
        if (isVersion3()) {
            return false;
        }

        return getPrimefacesBuildVersion().startsWith("4");
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
        ApplicationContext context = RequestContext.getCurrentInstance().getApplicationContext();
        try {
            
            //acess getConfig()
            Method methodGetConfig = context.getClass().getDeclaredMethod("getConfig");
            Object config = methodGetConfig.invoke(context);
            //acess getBuildVersion()
            Method methodGetBuildVersion = config.getClass().getDeclaredMethod("getBuildVersion");
            String buildVersion = (String) methodGetBuildVersion.invoke(config);
            return buildVersion;
        } catch (NoSuchMethodException ex) {
            throw new FacesException(ex);
        } catch (SecurityException ex) {
            throw new FacesException(ex);
        } catch (IllegalAccessException ex) {
            throw new FacesException(ex);
        } catch (IllegalArgumentException ex) {
            throw new FacesException(ex);
        } catch (InvocationTargetException ex) {
            throw new FacesException(ex);
        }
    }

    public static boolean isVersion5() {
        if (isVersion3()) {
            return false;
        }
        return getPrimefacesBuildVersion().startsWith("5");
    }

    public static boolean isVersion3() {
        if (IS_VERSION_3 != null) {
            return IS_VERSION_3;
        } else {
            try {
                Class classConstants = Class.forName("org.primefaces.util.Constants");
                Field fieldVersion;
                fieldVersion = classConstants.getDeclaredField("VERSION");
                if (fieldVersion.get(null).toString().startsWith("3")) {
                    IS_VERSION_3 = true;
                }
            } catch (Exception ex) {
                IS_VERSION_3 = false;
            }

        }
        return IS_VERSION_3;
    }

    /**
     * @param targets
     * @deprecated use update instead
     */
    @Deprecated
    public static void addPartialUpdateTarget(String... targets) {
        RequestContext context = RequestContext.getCurrentInstance();
        if (context != null) {
            for (String string : targets) {
                context.update(string);
            }
        }
    }
}
