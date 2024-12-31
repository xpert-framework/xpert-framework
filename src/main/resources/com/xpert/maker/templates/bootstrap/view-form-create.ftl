<ui:composition  xmlns="http://www.w3.org/1999/xhtml"
                 xmlns:h="jakarta.faces.html"
                 xmlns:f="jakarta.faces.core"
                 xmlns:ui="jakarta.faces.facelets"
                 xmlns:p="http://primefaces.org/ui"
                 xmlns:x="http://xpert.com/faces">
    
  
    <h:form id="formCreate${entity.name}" >
        <p:fieldset legend="${sharp}{xmsg['generalData']}">
            <x:modalMessages/>

            <div class="container-fluid">
                 <div class="row">
                 <#list entity.fields as field>
                 <#if field.renderFieldInFormCreate == true >
                      <div class="form-group ${configuration.bootstrapVersion.defaultColumns}">
                         <#-- Boolean -->
                         <#if field.yesNo == true>
                         <div class="checkbox">
                             <label>
                                 <h:selectBooleanCheckbox id="${field.label}" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" />
                                 ${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}
                             </label>
                         </div>
                         </#if>
                         <#-- Others -->
                         <#if field.yesNo == false>
                         <#-- String -->
                         <#if field.string == true>
                         <h:outputLabel for="${field.label}" value="<#if field.required == true>* </#if>${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}:" />
                         <p:inputText id="${field.label}" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" maxlength="${field.maxlength?string}"  styleClass="form-control"  />
                         </#if>
                         <#-- Integer/Long -->
                         <#if field.integer == true>
                         <h:outputLabel for="${field.label}" value="<#if field.required == true>* </#if>${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}:" />
                         <p:inputMask id="${field.label}" mask="9?999999999" ${configuration.slotCharName}="" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" styleClass="form-control" />
                         </#if>
                         <#-- Decimal (BigDecimal, Double) -->
                         <#if field.decimal == true>
                         <h:outputLabel for="${field.label}" value="<#if field.required == true>* </#if>${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}:" />
                         <x:inputNumber id="${field.label}" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" styleClass="form-control"/>
                         </#if>
                         <#-- Date p:calendar wont work with form-control, he has a span over the input and class goes for span -->
                         <#if field.date == true>
                         <h:outputLabel for="${field.label}" value="<#if field.required == true>* </#if>${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}:" />
                         <#if configuration.maskCalendar == true>
                         <p:calendar id="${field.label}" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" 
                                     showOn="button" pattern="${configuration.datePattern}" styleClass="uix-calendar"  >
                             <x:mask/>
                         </p:calendar>
                         <#else>
                         <p:calendar id="${field.label}" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" 
                                     showOn="button" pattern="${configuration.datePattern}" styleClass="uix-calendar" />
                         </#if>
                         </#if>
                          <#-- Time -->
                         <#if field.time == true>
                         <h:outputLabel for="${field.label}" value="<#if field.required == true>* </#if>${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}:" />
                         <#if configuration.maskCalendar == true>
                         <p:calendar id="${field.label}" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" 
                                     showOn="button" pattern="${configuration.timePattern}" timeOnly="true" styleClass="uix-calendar">
                             <x:mask/>
                         </p:calendar>
                         <#else>
                         <p:calendar id="${field.label}" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" 
                                     showOn="button" pattern="${configuration.timePattern}" timeOnly="true" styleClass="uix-calendar"/>
                         </#if>
                         </#if>
                         <#-- Enuns/ManyToOne (render a combobox) -->
                         <#if field.enumeration == true || field.manyToOne == true>
                         <h:outputLabel for="${field.label}" value="<#if field.required == true>* </#if>${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}:" />
                         <h:selectOneMenu id="${field.label}" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" <#if field.enumeration == false>converter="entityConverter"</#if> styleClass="form-control" >
                             <#if field.lazy == true>
                             <x:initializer/>
                             </#if>
                             <f:selectItem itemLabel="${sharp}{xmsg['select']}" />
                             <f:selectItems value="${sharp}{findAllBean.get(class${configuration.managedBeanSuffix}.${field.typeNameLower})}" 
                                            var="${field.typeNameLower}"
                                            itemLabel="${sharp}{${field.typeNameLower}}"/>
                         </h:selectOneMenu>
                         </#if>
                         <#-- Collections (render a checkbox list) -->
                         <#if field.manyToMany == true >
                         <h:outputLabel value="<#if field.required == true>* </#if>${sharp}{${resourceBundle}['${entity.nameLower}.${field.label}']}:" />
                         <h:selectManyCheckbox id="${field.label}" value="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity.${field.name}}" converter="entityConverter" >
                             <#if field.lazy == true>
                             <x:initializer/>
                             </#if>
                             <f:selectItems value="${sharp}{findAllBean.get(class${configuration.managedBeanSuffix}.${field.typeNameLower})}" 
                                            var="${field.typeNameLower}"
                                            itemLabel="${sharp}{${field.typeNameLower}}"/>
                         </h:selectManyCheckbox>
                         </#if>
                         </#if>
                     </div>
                 </#if>
                 </#list>

                 </div>
             </div>
        </p:fieldset>
        <h:outputText value="${sharp}{xmsg['requiredFieldsForm']}"/>
        <div class="uix-center">
            <#if configuration.generatesSecurityArea == true >
            <x:securityArea rolesAllowed="${entity.nameLower}.create">
                <p:commandButton process="@form" update="@form" action="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.save}" value="${sharp}{xmsg['save']}" />
            </x:securityArea>
            <x:securityArea rolesAllowed="${entity.nameLower}.audit">
                <x:audit entity="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity}"/>
            </x:securityArea>
            <#else>
            <p:commandButton process="@form" update="@form" action="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.save}" value="${sharp}{xmsg['save']}" />
            <x:audit entity="${sharp}{${entity.nameLower}${configuration.managedBeanSuffix}.entity}"/>
            </#if>
        </div>
    </h:form>

</ui:composition>