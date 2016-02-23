package com.xpert.faces.component.pdfprinter;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.behavior.ClientBehaviorBase;
import javax.faces.component.behavior.ClientBehaviorContext;

/**
 *
 * @author ayslan
 */
@ResourceDependencies({
    @ResourceDependency(library = "xpert", name = "scripts/core.js"),
    @ResourceDependency(library = "xpert", name = "css/style.css")
})
public class PDFPrinterBehavior extends ClientBehaviorBase {

    private String target;

    public PDFPrinterBehavior(String target) {
        this.target = target;
    }

    public PDFPrinterBehavior() {
    }

    @Override
    public String getScript(ClientBehaviorContext behaviorContext) {

        StringBuilder script = new StringBuilder();

        String clientId = behaviorContext.getComponent().getClientId();
        String componentTarget =  behaviorContext.getComponent().findComponent(target).getClientId();
        
        script.append("Xpert.printPDF('").append(clientId).append("', '").append(componentTarget).append("');");

        return script.toString();
    }

    public String getTarget() {
        return target;
    }

    public void setTarget(String target) {
        this.target = target;
    }

}
