<ui:composition  xmlns="http://www.w3.org/1999/xhtml"
                 xmlns:h="jakarta.faces.html"
                 xmlns:f="jakarta.faces.core"
                 xmlns:ui="jakarta.faces.facelets"
                 xmlns:p="http://primefaces.org/ui"
                 template="${template}"
                 xmlns:x="http://xpert.com/faces">
    <ui:param name="title" value="${sharp}{${resourceBundle}['${entity.nameLower}.list']}" />
    <ui:define name="body">
        <ui:include src="menu${entity.name}.xhtml" />
        <h:form id="formList${entity.name}">
            <x:modalMessages/>

            <x:dataTableActions fileName="${entity.nameLower}_export" target=":formList${entity.name}:dataTable${entity.name}"
                                     widgetVar="${entity.widgetVarDataTableName}"  />

            <p:dataTable paginator="true" rows="10" rowsPerPageTemplate="10,20,30" paginatorPosition="bottom" emptyMessage="${sharp}{xmsg['noRecordFound']}"
                         var="${entity.nameLower}" rowIndexVar="index" id="dataTable${entity.name}" widgetVar="${entity.widgetVarDataTableName}" styleClass="table-responsive"
                         currentPageReportTemplate="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.dataModel.currentPageReportTemplate}"
                         paginatorTemplate="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.dataModel.paginatorTemplate}"
                         value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.dataModel}" lazy="true" >
                <p:column styleClass="uix-datatable-index">
                    <f:facet name="header">-</f:facet>
                    <h:outputText value="${sharp}{index+1}"/>
                </p:column>
                <#list entity.fields as field>
                <#if field.collection == false && field.id == false>
                <p:column headerText="${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}" sortBy="${sharp}{${entity.nameLower}.${field.name}}"
                    <#if field.string == true || field.integer == true || field.enumeration == true || field.yesNo == true || field.date == true>filterBy="${sharp}{${entity.nameLower}.${field.name}}"</#if> <#if field.yesNo == true>filterOptions="${sharp}{booleanSelectItensEmptyOption}"</#if> <#if field.enumeration == true>filterOptions="${sharp}{findAllBean.getSelect(class${configuration.managedBeanSuffix}.${field.typeNameLower})}"</#if> <#if field.date == true || field.time == true || field.yesNo == true>style="text-align: center;"</#if><#if field.decimal == true>style="text-align: right;"</#if>>
                    <f:facet name="header">
                        <h:outputText value="${sharp}{${resourceBundle}['${entity.nameLower}.${field.name}']}" />
                       <#if field.date == true>
                        <x:dateFilter/>
                        </#if>
                    </f:facet>
                    <#if field.lazy == true>
                    <h:outputText value="${sharp}{${entity.nameLower}.${field.name}}">
                        <x:initializer/>
                    </h:outputText>
                    </#if>
                    <#if field.decimal == true>
                    <h:outputText value="${sharp}{${entity.nameLower}.${field.name}}">
                        <f:convertNumber minFractionDigits="2" maxFractionDigits="2" />
                    </h:outputText>
                    </#if>
                    <#if field.date == true>
                    <h:outputText value="${sharp}{${entity.nameLower}.${field.name}}">
                        <f:convertDateTime />
                    </h:outputText>
                    </#if>
                    <#if field.time == true>
                    <h:outputText value="${sharp}{${entity.nameLower}.${field.name}}">
                        <f:convertDateTime pattern="${configuration.timePattern}"/>
                    </h:outputText>
                    </#if>
                    <#if field.yesNo == true>
                    <h:outputText value="${sharp}{${entity.nameLower}.${field.name}}" converter ="yesNoConverter" />
                    </#if>
                    <#if field.lazy == false && field.decimal == false && field.date == false && field.time == false && field.yesNo == false>
                    <h:outputText value="${sharp}{${entity.nameLower}.${field.name}}"/>
                    </#if>
                </p:column>
                </#if>
                </#list>
                <p:column styleClass="uix-datatable-actions" exportable="false" headerText="${sharp}{xmsg['actions']}">
                    <f:facet name="header">
                        <h:outputText value="${sharp}{xmsg['actions']}"/>
                    </f:facet>
                    <p:commandButton oncomplete="${entity.widgetVarDetail}.show();"  icon="${sharp}{icons.detail}" 
                                     process="@form" update=":formDetail${entity.name}" title="${sharp}{xmsg['detail']}" >
                        <f:setPropertyActionListener value="${sharp}{${entity.nameLower}}" target="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity}" />
                    </p:commandButton>
                    <#if entity.embeddedId == false && configuration.hideIdInRequest == false >
                    <#if configuration.generatesSecurityArea == true >
                    <x:securityArea rolesAllowed="${entity.nameLower}.create">
                        <p:button icon="${sharp}{icons.edit}" outcome="create${entity.name}" title="${sharp}{xmsg['edit']}">
                            <f:param name="id" value="${sharp}{${entity.nameLower}.${entity.idFieldName}}" />
                        </p:button>
                    </x:securityArea>
                    <#else>
                    <p:button icon="${sharp}{icons.edit}" outcome="create${entity.name}" title="${sharp}{xmsg['edit']}">
                        <f:param name="id" value="${sharp}{${entity.nameLower}.${entity.idFieldName}}" />
                    </p:button>
                    </#if>
                    <#else>
                    <#if configuration.generatesSecurityArea == true >
                    <x:securityArea rolesAllowed="${entity.nameLower}.create">
                        <p:commandButton icon="${sharp}{icons.edit}"  action="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.putEntityInRequest}"
                                         ajax="false" title="${sharp}{xmsg['edit']}" >
                            <f:setPropertyActionListener value="${sharp}{${entity.nameLower}}" target="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity}" />
                            <f:setPropertyActionListener value="create${entity.name}" target="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.outcome}" />
                        </p:commandButton>
                    </x:securityArea>
                    <#else>
                    <p:commandButton icon="${sharp}{icons.edit}"  action="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.putEntityInRequest}"
                                     ajax="false" title="${sharp}{xmsg['edit']}" >
                        <f:setPropertyActionListener value="${sharp}{${entity.nameLower}}" target="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity}" />
                        <f:setPropertyActionListener value="create${entity.name}" target="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.outcome}" />
                    </p:commandButton>
                    </#if>
                    </#if>
                    <#if configuration.generatesSecurityArea == true >
                    <x:securityArea rolesAllowed="${entity.nameLower}.delete">
                        <p:commandButton icon="${sharp}{icons.delete}" title="${sharp}{xmsg['delete']}" process="@form" update="@form" 
                                         action="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.delete}" >
                            <f:setPropertyActionListener value="${sharp}{${entity.nameLower}.${entity.idFieldName}}" target="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.id}" />
                            <p:confirm message="${sharp}{xmsg['confirmDelete']} - ${sharp}{${entity.nameLower}}" />
                        </p:commandButton>
                    </x:securityArea>
                    <#else>
                    <p:commandButton icon="${sharp}{icons.delete}" title="${sharp}{xmsg['delete']}" process="@form" update="@form" 
                                     action="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.delete}" >
                        <f:setPropertyActionListener value="${sharp}{${entity.nameLower}.${entity.idFieldName}}" target="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.id}" />
                        <p:confirm message="${sharp}{xmsg['confirmDelete']} - ${sharp}{${entity.nameLower}}" />
                    </p:commandButton>
                    </#if>
                </p:column>
            </p:dataTable>
            <div class="uix-audit-delete">
            <#if configuration.generatesSecurityArea == true >
                <x:securityArea rolesAllowed="${entity.nameLower}.audit">
                    <x:auditDelete entityClass="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entityClass}"/>
                </x:securityArea>
            <#else>
                <x:auditDelete entityClass="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entityClass}"/>
            </#if>
            </div>  
        </h:form>

        <p:dialog widgetVar="${entity.widgetVarDetailName}" header="${sharp}{${resourceBundle}['${entity.nameLower}.detail']}" ${entity.appendTo} 
                  modal="true" <#if configuration.bootstrapVersion?? >styleClass="uix-dialog-fluid"<#else>height="500" width="800"</#if> >
                  <ui:include src="detail${entity.name}.xhtml" />
        </p:dialog>
    </ui:define>
</ui:composition>