<ui:composition xmlns="http://www.w3.org/1999/xhtml"
                dir="ltr" lang="en-US" xml:lang="en"
                xmlns:h="http://java.sun.com/jsf/html"
                xmlns:f="http://java.sun.com/jsf/core"
                xmlns:ui="http://java.sun.com/jsf/facelets"
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