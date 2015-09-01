package com.xpert.faces.component.pdfprinter;

import com.xpert.faces.utils.FacesUtils;
import javax.el.ELContext;
import javax.el.ValueExpression;
import javax.faces.FacesException;
import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.StateHolder;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

@ResourceDependencies({
    @ResourceDependency(library = "xpert", name = "scripts/core.js"),
    @ResourceDependency(library = "xpert", name = "css/style.css")
})
public class PDFPrinter implements ActionListener, StateHolder {

    private ValueExpression target;
    private ValueExpression fileName;
    private ValueExpression orientation;
    private ValueExpression cacheCss;
    private ValueExpression replaceHttps;

    private static final String HTML_PARAMETER_NAME = "xpert_html_export";

    public PDFPrinter() {
    }

    public PDFPrinter(ValueExpression target, ValueExpression fileName, ValueExpression orientation, ValueExpression cacheCss, ValueExpression replaceHttps) {
        this.target = target;
        this.fileName = fileName;
        this.orientation = orientation;
        this.cacheCss = cacheCss;
        this.replaceHttps = replaceHttps;
    }

    @Override
    public void processAction(ActionEvent event) {

        FacesContext context = FacesContext.getCurrentInstance();
        ELContext elContext = context.getELContext();

        String targetId = (String) target.getValue(elContext);
        String outputFileName = null;
        if (fileName != null) {
            outputFileName = (String) fileName.getValue(elContext);
        } else {
            outputFileName = "no_name";
        }

        String pageOrientationString = null;
        if (orientation != null) {
            pageOrientationString = (String) orientation.getValue(elContext);
        }
        PageOrientation pageOrientation = PageOrientation.PORTRAIT;
        if (pageOrientationString != null) {
            if (pageOrientationString.toUpperCase().equals(PageOrientation.LANDSCAPE.name())) {
                pageOrientation = PageOrientation.LANDSCAPE;
            }
        }

        Boolean cacheCssValue = true;
        if (cacheCss != null) {
            cacheCssValue = (Boolean) cacheCss.getValue(elContext);
        }
        
        Boolean replaceHttpsValue = true;
        if (replaceHttps != null) {
            cacheCssValue = (Boolean) replaceHttps.getValue(elContext);
        }

        try {

            UIComponent component = event.getComponent().findComponent(targetId);
            if (component == null) {
                throw new FacesException("Cannot find component " + targetId + " in view.");
            }

            String htmlParameter = FacesUtils.getParameter(HTML_PARAMETER_NAME);
            byte[] pdf = PDFPrinterBuilder.createPDF(context, htmlParameter, pageOrientation, cacheCssValue, replaceHttpsValue);

            FacesUtils.download(pdf, "application/pdf", outputFileName.endsWith(".pdf") ? outputFileName : outputFileName + ".pdf");
            context.responseComplete();
        } catch (Exception ex) {
            throw new FacesException(ex);
        }
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Override
    public void setTransient(boolean value) {
    }

    @Override
    public void restoreState(FacesContext context, Object state) {
        Object values[] = (Object[]) state;

        target = (ValueExpression) values[0];
        fileName = (ValueExpression) values[1];
        orientation = (ValueExpression) values[2];
        cacheCss = (ValueExpression) values[3];
        replaceHttps = (ValueExpression) values[4];
    }

    @Override
    public Object saveState(FacesContext context) {
        Object values[] = new Object[8];

        values[0] = target;
        values[1] = fileName;
        values[2] = orientation;
        values[3] = cacheCss;
        values[4] = replaceHttps;

        return ((Object[]) values);
    }
}
