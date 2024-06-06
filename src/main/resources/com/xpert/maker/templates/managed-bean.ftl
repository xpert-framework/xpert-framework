package ${configuration.managedBean};


import java.io.Serializable;
import com.xpert.core.crud.AbstractBaseBean;
import jakarta.ejb.EJB;
<#if configuration.useCDIBeans == false >
import jakarta.faces.bean.ManagedBean;
import jakarta.faces.bean.ViewScoped;
</#if>
<#if configuration.useCDIBeans == true >
import jakarta.faces.view.ViewScoped;
import jakarta.inject.Named; 
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
