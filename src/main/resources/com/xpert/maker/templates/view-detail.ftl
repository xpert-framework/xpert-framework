<ui:composition  xmlns="http://www.w3.org/1999/xhtml"
                 xmlns:h="http://java.sun.com/jsf/html"
                 xmlns:f="http://java.sun.com/jsf/core"
                 xmlns:ui="http://java.sun.com/jsf/facelets"
                 xmlns:p="http://primefaces.org/ui"
                 xmlns:x="http://xpert.com/faces">
    <h:form id="formDetail${entity.name}">
        <h:panelGrid columns="4" styleClass="grid-detail">
        <#list entity.fields as field>
            <#if field.collection == false && field.id == false>
           
            <h:outputLabel value="${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}:" />
            <#if field.lazy == true>
            <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}">
                <x:initializer/>
            </h:outputText>
            </#if>
            <#if field.decimal == true>
            <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}">
                <f:convertNumber minFractionDigits="2" maxFractionDigits="2" />
            </h:outputText>
            </#if>
            <#if field.date == true>
            <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}">
                <f:convertDateTime />
            </h:outputText>
            </#if>
            <#if field.time == true>
            <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}">
                <f:convertDateTime pattern="${configuration.timePattern}"/>
            </h:outputText>
            </#if>
            <#if field.yesNo == true>
            <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" converter ="yesNoConverter" />
            </#if>
            <#if field.lazy == false && field.decimal == false && field.date == false && field.time == false && field.yesNo == false>
            <h:outputText value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}"/>
            </#if>
            </#if>
        </#list>

        </h:panelGrid>
        <p:separator/>
        <div style="text-align: center;">
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