package com.xpert.maker;

/**
 *
 * @author Ayslan
 */
public enum BeanType {

    MANAGED_BEAN("managed-bean.ftl", "java", false),
    BUSINESS_OBJECT("business-object.ftl", "java", false),
    DAO("dao.ftl", "java", false),
    DAO_IMPL("dao-impl.ftl", "java", false),
    //xhtml
    VIEW_LIST("view-list.ftl", "xhtml", true, true, false),
    VIEW_DETAIL("view-detail.ftl", "xhtml", true, true, true),
    VIEW_FORM_CREATE("view-form-create.ftl", "xhtml", true, false, true),
    VIEW_MENU("view-menu.ftl", "xhtml", true),
    VIEW_CREATE("view-create.ftl", "xhtml", true);
    
    private String template;
    private String extension;
    private boolean view;
    private boolean primefacesVersionDependend;
    private boolean bootstrapDependend;

    private BeanType(String template, String extension, boolean view) {
        this.template = template;
        this.extension = extension;
        this.view = view;
    }

    private BeanType(String template, String extension, boolean view, boolean primefacesVersionDependend, boolean bootstrapDependend) {
        this.template = template;
        this.extension = extension;
        this.view = view;
        this.primefacesVersionDependend = primefacesVersionDependend;
        this.bootstrapDependend = bootstrapDependend;
    }
    
    

    public String getTemplate() {
        return template;
    }

    public String getExtension() {
        return extension;
    }

    public boolean isView() {
        return view;
    }

    public boolean isPrimefacesVersionDependend() {
        return primefacesVersionDependend;
    }

    public boolean isBootstrapDependend() {
        return bootstrapDependend;
    }
    
    
    
}
