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
public final class Icons extends HashMap<String, String> implements Serializable {

    private static final long serialVersionUID = 5099651664242215465L;

    public Icons() {
        fontAwesome();
    }

    /**
     * Configure default icons for primefaces icons
     *
     * @return
     */
    public Icons primeFaces() {

        this.audit("pi pi-clock")
                .refresh("pi pi-refresh")
                .close("pi pi-times")
                .plus("pi pi-plus")
                .create("pi pi-plus")
                .delete("pi pi-trash")
                .detail("pi pi-search-plus")
                .edit("pi pi-pen-to-square")
                .exportOptions("pi pi-file-export")
                .search("pi pi-search")
                .excel("pi pi-file-excel")
                .csv("pi pi-file-o")
                .pdf("pi pi-file-pdf");

        return this;
    }

    /**
     * Configure default icons for font-awesome
     *
     * @return
     */
    public Icons fontAwesome() {

        this.audit("fa fa-fw fa-clock-rotate-left")
                .refresh("fa fa-fw fa-arrows-rotate")
                .close("fa fa-fw fa-xmark")
                .plus("fa fa-fw fa-plus")
                .create("fa fa-fw fa-plus")
                .delete("fa-regular fa-fw fa-trash-can")
                .detail("fa fa-fw fa-magnifying-glass")
                .edit("fa fa-fw fa-pen-to-square")
                .exportOptions("fa fa-fw fa-file-export")
                .search("fa fa-fw fa-magnifying-glass")
                .excel("fa-regular fa-fw fa-file-excel")
                .csv("fa fa-fw fa-file-csv")
                .pdf("fa-regular fa-fw fa-file-pdf");

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
