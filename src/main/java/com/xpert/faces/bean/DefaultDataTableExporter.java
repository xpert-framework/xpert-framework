package com.xpert.faces.bean;

import com.lowagie.text.BadElementException;
import com.lowagie.text.Document;
import com.lowagie.text.DocumentException;
import com.lowagie.text.Image;
import com.xpert.faces.utils.FacesUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;

/**
 * Default Managed Bean to process export. This bean add manipulates the Object
 * in pre/post events in export.
 *
 * @author ayslan
 */
@Named("defaultDataTableExporter")
@RequestScoped
public class DefaultDataTableExporter implements Serializable {

    private static final long serialVersionUID = -1939675915570705879L;

    public static final String LOGO_PATH_PNG = "/images/logo.png";
    public static final String LOGO_PATH_JPG = "/images/logo.jpg";

    public void preProcessorPDF(Object document) throws IOException,
            BadElementException, DocumentException {
        if (document != null) {
            //try to get PNG
            String logoPath = FacesUtils.getRealPath(LOGO_PATH_PNG);
            //if not found try to get JPG
            if (logoPath == null || logoPath.isEmpty() || !new File(logoPath).exists()) {
                logoPath = FacesUtils.getRealPath(LOGO_PATH_JPG);
                if (logoPath == null || logoPath.isEmpty() || !new File(logoPath).exists()) {
                    return;
                }
            }
            Document pdf = (Document) document;
            pdf.open();
            Image image = Image.getInstance(logoPath);
            image.scalePercent(50F);
            pdf.add(image);
        }
    }

    public void postProcessorPDF(Object document) throws IOException, BadElementException, DocumentException {
        //The default implamentation does nothing
    }

    public void preProcessorExcel(Object document) throws IOException, BadElementException, DocumentException {
        //The default implamentation does nothing
    }

    public void postProcessorExcel(Object document) throws IOException, BadElementException, DocumentException {
        //The default implamentation does nothing
    }

}
