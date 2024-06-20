<ui:composition  xmlns="http://www.w3.org/1999/xhtml"
                 xmlns:h="http://java.sun.com/jsf/html"
                 xmlns:f="http://java.sun.com/jsf/core"
                 xmlns:ui="http://java.sun.com/jsf/facelets"
                 xmlns:p="http://primefaces.org/ui"
                 xmlns:x="http://xpert.com/faces">
    <h:form id="formDetail${entity.name}" styleClass="uix-form-detail">
        <div class="container-fluid">
            <div class="row">
            <#list entity.fields as field>
                <#if field.collection == false && field.id == false>
                <div class="${configuration.bootstrapVersion.defaultColumns}">
                    <h:outputLabel value="${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}:" styleClass="control-label" /><br/>
                    <#if field.lazy == true>
                    <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" styleClass="uix-content-detail">
                        <x:initializer/>
                    </h:outputText>
                    </#if>
                    <#if field.decimal == true>
                    <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" styleClass="uix-content-detail">
                        <f:convertNumber minFractionDigits="2" maxFractionDigits="2" />
                    </h:outputText>
                    </#if>
                    <#if field.date == true>
                    <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" styleClass="uix-content-detail">
                        <f:convertDateTime />
                    </h:outputText>
                    </#if>
                    <#if field.time == true>
                    <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" styleClass="uix-content-detail">
                        <f:convertDateTime pattern="${configuration.timePattern}"/>
                    </h:outputText>
                    </#if>
                    <#if field.yesNo == true>
                    <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" converter ="yesNoConverter"  styleClass="uix-content-detail"/>
                    </#if>
                    <#if field.lazy == false && field.decimal == false && field.date == false && field.time == false && field.yesNo == false>
                    <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" styleClass="uix-content-detail"/>
                    </#if>
                </div>
                </#if>
            </#list>
           </div>
         </div>
        <p:separator/>
        <div class="uix-center">
            <p:commandButton type="button" value="${sharp}{xmsg['close']}" onclick="${entity.widgetVarDetail}.hide()" />
            <#if configuration.generatesSecurityArea == true >
            <x:securityArea rolesAllowed="${entity.nameLower}.audit">
                <x:audit entity="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity}"/>
            </x:securityArea>
            <#else>
            <x:audit entity="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity}"/>
            </#if>
        </div>
    </h:form>
</ui:composition>