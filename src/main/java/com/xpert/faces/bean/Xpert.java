package com.xpert.faces.bean;

import com.xpert.Constants;
import com.xpert.faces.primefaces.PrimeFacesUtils;
import com.xpert.i18n.XpertResourceBundle;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.Serializable;

/**
 * Generic managed bean to acess utils methods and funtions from xpert-framework
 *
 * @author ayslan
 */
@Named("xpert")
@RequestScoped
public class Xpert implements Serializable {

    private static final long serialVersionUID = 1065050959497205171L;

    public static final String DEFAULT_PAGINATOR_TEMPLATE = "{CurrentPageReport} {FirstPageLink} {PreviousPageLink} {PageLinks} {NextPageLink} {LastPageLink} {RowsPerPageDropdown}";
    public static final String UNKNOW_COUNT_PAGINATOR_TEMPLATE = "{PreviousPageLink} {NextPageLink} {RowsPerPageDropdown} {CurrentPageReport}";

    public String getVersion() {
        return Constants.VERSION;
    }

    public String normalizePrimeFacesWidget(String widgetVar) {
        return PrimeFacesUtils.normalizeWidgetVar(widgetVar);
    }

    /**
     * Default "CurrentPageReportTemplate" of primefaces p:datatable
     * #{xpert.defaultCurrentPageReportTemplate}
     *
     * @return template for current page
     */
    public String getDefaultCurrentPageReportTemplate() {
        return "{totalRecords} " + XpertResourceBundle.get("records") + " (" + XpertResourceBundle.get("page") + " {currentPage} " + XpertResourceBundle.get("of") + " {totalPages})";
    }

    /**
     * "CurrentPageReportTemplate" of primefaces p:datatable when there is no
     * count defined in table #{xpert.defaultCurrentPageReportTemplate}
     *
     * @return template for current page
     */
    public String getUnknowCountCurrentPageReportTemplate() {
        return XpertResourceBundle.get("page") + " {currentPage} ";
    }

    /**
     * Default "PaginatorTemplate" of primefaces p:datatable
     * #{xpert.defaultCurrentPageReportTemplate}
     *
     * @return template for paginator template
     */
    public String getDefaultPaginatorTemplate() {
        return DEFAULT_PAGINATOR_TEMPLATE;
    }

    /**
     * "PaginatorTemplate" of primefaces p:datatable when there is no count
     * defined
     *
     * @return template for paginator template
     */
    public String getUnknowCountPaginatorTemplate() {
        return UNKNOW_COUNT_PAGINATOR_TEMPLATE;
    }

}
