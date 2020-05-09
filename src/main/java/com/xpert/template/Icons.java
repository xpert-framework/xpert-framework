package com.xpert.template;

import java.util.HashMap;

/**
 * Icons is a ManagedBeans (it can be used as a Map) to get icons in application
 *
 * @author ayslanms
 */
public class Icons extends HashMap<String, String> {

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
                .create("pi pi-plus")
                .delete("pi pi-trash")
                .detail("pi pi-search-plus")
                .edit("pi pi-pencil")
                .exportOptions("pi pi-chevron-down")
                .search("pi pi-search");

        return this;
    }

    /**
     * Configure default icons for jquery-ui (legacy primefaces icons)
     *
     * @return
     */
    public final Icons jqueryUi() {

        this.audit("ui-icon-plus")
                .create("ui-icon-plus")
                .delete("ui-icon-trash")
                .detail("ui-icon-zoomin")
                .edit("ui-icon-pencil")
                .exportOptions("ui-icon-arrowthickstop-1-s")
                .search("ui-icon-search");

        return this;
    }

    /**
     * Configure default icons for font-awesome
     *
     * @return
     */
    public Icons fontAwesome() {

        this.audit("fas fa-plus")
                .create("fas fa-plus")
                .delete("fa fa-trash")
                .detail("fas fa-search-plus")
                .edit("fas fa-edit")
                .exportOptions("fas fa-chevron-down")
                .search("fas fa-search");

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

}
