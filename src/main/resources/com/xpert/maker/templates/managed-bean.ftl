package ${configuration.managedBean};


import java.io.Serializable;
import com.xpert.core.crud.AbstractBaseBean;
import javax.ejb.EJB;
<#if configuration.useCDIBeans == false >
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
</#if>
<#if configuration.useCDIBeans == true >
import javax.faces.view.ViewScoped;
import javax.inject.Named; 
</#if>
import ${configuration.businessObject}.${name}${configuration.businessObjectSuffix};
import ${entity.name};

/**
 *
 * @author ${author}
 */
<#if configuration.useCDIBeans == false >
@ManagedBean
</#if>
<#if configuration.useCDIBeans == true >
@Named
</#if>
@ViewScoped
public class ${name}${configuration.managedBeanSuffix} extends AbstractBaseBean<${name}> implements Serializable {

    @EJB
    private ${name}${configuration.businessObjectSuffix} ${nameLower}${configuration.businessObjectSuffix};

    @Override
    public ${name}${configuration.businessObjectSuffix} getBO() {
        return ${nameLower}${configuration.businessObjectSuffix};
    }

    @Override
    public String getDataModelOrder() {
        return "${viewEntity.idFieldName}";
    }
    <#if configuration.hideIdInRequest == true >

    @Override
    public boolean isLoadEntityOnPostConstruct() {
        return false;
    }
    </#if>
}
