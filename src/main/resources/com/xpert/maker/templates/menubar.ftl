<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                dir="ltr" lang="en-US" xml:lang="en"
                xmlns:h="jakarta.faces.html"
                xmlns:f="jakarta.faces.core"
                xmlns:ui="jakarta.faces.facelets"
                xmlns:p="http://primefaces.org/ui"
                xmlns:x="http://xpert.com/faces">

         <p:menubar>
            <p:submenu label="CRUD">
                <#list menus as menu>
                <p:menuitem url="${menu.url}" value="${menu.label}"/>
                </#list>
            </p:submenu>
        </p:menubar>

</ui:composition>