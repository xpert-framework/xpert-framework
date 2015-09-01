package com.xpert.faces.component.pdfprinter;

import java.io.IOException;

import javax.el.ELException;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.component.ActionSource;
import javax.faces.component.UIComponent;
import javax.faces.component.behavior.ClientBehaviorHolder;
import javax.faces.view.facelets.ComponentHandler;
import javax.faces.view.facelets.FaceletContext;
import javax.faces.view.facelets.FaceletException;
import javax.faces.view.facelets.TagAttribute;
import javax.faces.view.facelets.TagConfig;
import javax.faces.view.facelets.TagHandler;

public class PDFPrinterTagHandler extends TagHandler {

    private final TagAttribute target;
    private final TagAttribute fileName;
    private final TagAttribute orientation;
    private final TagAttribute cacheCss;
    private final TagAttribute replaceHttps;

    public PDFPrinterTagHandler(TagConfig tagConfig) {
        super(tagConfig);
        this.target = getRequiredAttribute("target");
        this.fileName = getAttribute("fileName");
        this.orientation = getAttribute("orientation");
        this.cacheCss = getAttribute("cacheCss");
        this.replaceHttps = getAttribute("replaceHttps");
    }

    public void apply(FaceletContext faceletContext, UIComponent parent) throws IOException, FacesException, FaceletException, ELException {
        if (ComponentHandler.isNew(parent)) {
            ValueExpression targetVE = target.getValueExpression(faceletContext, Object.class);
            ValueExpression fileNameVE = null;
            if (fileName != null) {
                fileNameVE = fileName.getValueExpression(faceletContext, Object.class);
            }
            ValueExpression orientationVE = null;
            if (orientation != null) {
                orientationVE = orientation.getValueExpression(faceletContext, Object.class);
            }
            ValueExpression cacheCssVE = null;
            if (cacheCss != null) {
                cacheCssVE = cacheCss.getValueExpression(faceletContext, Boolean.class);
            }

            ValueExpression replaceHttpsVE = null;
            if (replaceHttps != null) {
                replaceHttpsVE = replaceHttps.getValueExpression(faceletContext, Boolean.class);
            }

            ActionSource actionSource = (ActionSource) parent;
            actionSource.addActionListener(new PDFPrinter(targetVE, fileNameVE, orientationVE, cacheCssVE, replaceHttpsVE));

            ClientBehaviorHolder clientBehaviorHolder = (ClientBehaviorHolder) parent;
            clientBehaviorHolder.addClientBehavior("click", new PDFPrinterBehavior(target.getValue(faceletContext)));

        }
    }

}
