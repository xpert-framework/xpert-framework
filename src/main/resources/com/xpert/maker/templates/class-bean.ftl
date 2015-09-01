package ${package};

<#list classes as clazz>
import ${clazz.name};
</#list>
import javax.faces.bean.ManagedBean;

@ManagedBean
public class Class${configuration.managedBeanSuffix} {

    <#list classes as class>
    public Class get${class.simpleName}() {
        return ${class.simpleName}.class;
    }
    </#list>

}
