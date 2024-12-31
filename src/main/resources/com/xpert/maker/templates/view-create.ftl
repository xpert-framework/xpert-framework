<ui:composition  xmlns="http://www.w3.org/1999/xhtml"
                 xmlns:h="jakarta.faces.html"
                 xmlns:f="jakarta.faces.core"
                 xmlns:ui="jakarta.faces.facelets"
                 xmlns:p="http://primefaces.org/ui"
                 template="${template}"
                 xmlns:x="http://xpert.com/faces">
   <ui:param name="title" value="${sharp}{${resourceBundle}['${entity.nameLower}.create']}" />
   <ui:define name="body">
       <ui:include src="menu${entity.name}.xhtml" />
       <ui:include src="formCreate${entity.name}.xhtml" />
   </ui:define>
</ui:composition>