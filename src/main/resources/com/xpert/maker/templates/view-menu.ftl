<ui:composition  xmlns="http://www.w3.org/1999/xhtml"
                 xmlns:h="jakarta.faces.html"
                 xmlns:f="jakarta.faces.core"
                 xmlns:ui="jakarta.faces.facelets"
                 xmlns:p="http://primefaces.org/ui"
                 xmlns:x="http://xpert.com/faces">
    <p:toolbar>
        <p:toolbarGroup align="left">
            <#if configuration.generatesSecurityArea == true >
            <x:securityArea rolesAllowed="${entity.nameLower}.list">
                <p:button icon="${sharp}{icons.search}" value="${sharp}{xmsg['list']}" outcome="list${entity.name}" />
            </x:securityArea>
            <x:securityArea rolesAllowed="${entity.nameLower}.create">
                <p:button icon="${sharp}{icons.create}" value="${sharp}{xmsg['create']}" outcome="create${entity.name}" />
            </x:securityArea>
            <#else>
            <p:button icon="${sharp}{icons.search}" value="${sharp}{xmsg['list']}" outcome="list${entity.name}" />
            <p:button icon="${sharp}{icons.create}" value="${sharp}{xmsg['create']}" outcome="create${entity.name}" />
            </#if>
        </p:toolbarGroup>
    </p:toolbar>
</ui:composition>