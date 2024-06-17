package com.xpert.template;

import jakarta.ejb.Startup;
import jakarta.inject.Named;
import jakarta.inject.Singleton;
import java.io.Serializable;
import java.util.HashMap;

/**
 * Icons is a ManagedBeans (it can be used as a Map) to get icons in application
 *
 * @author ayslanms
 */
@Named("icons")
@Singleton
@Startup
public class Icons extends HashMap<String, String> implements Serializable {

    private static final long serialVersionUID = 5099651664242215465L;

    public Icons() {
        jqueryUi();
    }

    /**
     * Configure default icons for primefaces icons
     *
     * @return
     */
    public Icons primeFaces() {

        this.audit("pi pi-plus")
                .refresh("pi pi-refresh")
                .close("pi pi-times")
                .plus("pi pi-plus")
                .create("pi pi-plus")
                .delete("pi pi-trash")
                .detail("pi pi-search-plus")
                .edit("pi pi-pencil")
                .exportOptions("pi pi-chevron-down")
                .search("pi pi-search")
                .excel("pi pi-file-excel")
                .csv("pi pi-file")
                .pdf("pi pi-file-pdf");

        return this;
    }

    /**
     * Configure default icons for jquery-ui (legacy primefaces icons)
     *
     * @return
     */
    public final Icons jqueryUi() {

        this.audit("ui-icon-plus")
                .refresh("ui-icon-refresh")
                .close("ui-icon-close")
                .plus("ui-icon-plus")
                .create("ui-icon-plus")
                .delete("ui-icon-trash")
                .detail("ui-icon-zoomin")
                .edit("ui-icon-pencil")
                .exportOptions("ui-icon-arrowthickstop-1-s")
                .search("ui-icon-search")
                .excel("ui-icon-triangle-1-e")
                .csv("ui-icon-triangle-1-e")
                .pdf("ui-icon-triangle-1-e");

        return this;
    }

    /**
     * Configure default icons for font-awesome
     *
     * @return
     */
    public Icons fontAwesome() {

        this.audit("fas fa-plus")
                .refresh("pi pi-refresh")
                .close("pi pi-times")
                .plus("fas fa-plus")
                .create("fas fa-plus")
                .delete("fa fa-trash")
                .detail("fas fa-search-plus")
                .edit("fas fa-edit")
                .exportOptions("fas fa-file-export")
                .search("fas fa-search")
                .excel("fas fa-file-excel")
                .csv("fas fa-file-csv")
                .pdf("fas fa-file-pdf");

        return this;
    }

    /**
     * Configure "close" icon
     *
     * @param close
     * @return
     */
    public Icons close(String close) {
        this.put("close", close);
        return this;
    }

    /**
     * Configure "refresh" icon
     *
     * @param refresh
     * @return
     */
    public Icons refresh(String refresh) {
        this.put("refresh", refresh);
        return this;
    }

    /**
     * Configure "plus" icon
     *
     * @param plus
     * @return
     */
    public Icons plus(String plus) {
        this.put("plus", plus);
        return this;
    }

    /**
     * Configure "edit" icon
     *
     * @param edit
     * @return
     */
    public Icons edit(String edit) {
        this.put("edit", edit);
        return this;
    }

    /**
     * Configure "delete" icon
     *
     * @param delete
     * @return
     */
    public Icons delete(String delete) {
        this.put("delete", delete);
        return this;
    }

    /**
     * Configure "detail" icon
     *
     * @param detail
     * @return
     */
    public Icons detail(String detail) {
        this.put("detail", detail);
        return this;
    }

    /**
     * Configure "search" icon
     *
     * @param search
     * @return
     */
    public Icons search(String search) {
        this.put("search", search);
        return this;
    }

    /**
     * Configure "create" icon
     *
     * @param create
     * @return
     */
    public Icons create(String create) {
        this.put("create", create);
        return this;
    }

    /**
     * Configure "audit" icon
     *
     * @param audit
     * @return
     */
    public Icons audit(String audit) {
        this.put("audit", audit);
        return this;
    }

    /**
     * Configure "exportOptions" icon
     *
     * @param exportOptions
     * @return
     */
    public Icons exportOptions(String exportOptions) {
        this.put("exportOptions", exportOptions);
        return this;
    }

    /**
     * Configure "excel" icon
     *
     * @param excel
     * @return
     */
    public Icons excel(String excel) {
        this.put("excel", excel);
        return this;
    }

    /**
     * Configure "csv" icon
     *
     * @param csv
     * @return
     */
    public Icons csv(String csv) {
        this.put("csv", csv);
        return this;
    }

    /**
     * Configure "exportOptions" icon
     *
     * @param pdf
     * @return
     */
    public Icons pdf(String pdf) {
        this.put("pdf", pdf);
        return this;
    }

    /**
     * Return "audit" icon
     *
     * @return
     */
    public String getAudit() {
        return get("audit");
    }

    /**
     * Return "create" icon
     *
     * @return
     */
    public String getCreate() {
        return get("creates");
    }

    /**
     * Return "delete" icon
     *
     * @return
     */
    public String getDelete() {
        return get("delete");
    }

    /**
     * Return "detail" icon
     *
     * @return
     */
    public String getDetail() {
        return get("detail");
    }

    public String getEdit() {
        return get("edit");
    }

    /**
     * Return "export" icon
     *
     * @return
     */
    public String getExportOptions() {
        return get("exportOptions");
    }

    /**
     * Return "search" icon
     *
     * @return
     */
    public String getSearch() {
        return get("search");
    }

    /**
     * Return "plus" icon
     *
     * @return
     */
    public String getPlus() {
        return get("plus");
    }

    /**
     * Return "excel" icon
     *
     * @return
     */
    public String getExcel() {
        return get("excel");
    }

    /**
     * Return "csv" icon
     *
     * @return
     */
    public String getCsv() {
        return get("csv");
    }

    /**
     * Return "pdf" icon
     *
     * @return
     */
    public String getPdf() {
        return get("pdf");
    }

}
