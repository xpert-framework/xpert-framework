<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml"
      dir="ltr" lang="en-US" xml:lang="en"
      xmlns:h="http://xmlns.jcp.org/jsf/html"
      xmlns:f="http://xmlns.jcp.org/jsf/core"
      xmlns:ui="http://java.sun.com/jsf/facelets"
      xmlns:p="http://primefaces.org/ui"
      xmlns:x="http://xpert.com/faces">

    <f:view locale="${sharp}{localeBean.locale}">
        <h:head>
             <title>New Project<h:outputText rendered="${sharp}{not empty title}" value=" - ${sharp}{title}"/></title>
        </h:head>
        <h:body>
             <ui:insert name="body" />
        </h:body>
    </f:view>

</html>