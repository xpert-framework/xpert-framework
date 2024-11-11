package com.xpert.faces.bean;

import com.itextpdf.text.BadElementException;
import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Image;
import com.xpert.faces.utils.FacesUtils;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Named;
import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import org.primefaces.component.export.ExcelOptions;
import org.primefaces.component.export.PDFOptions;
import org.primefaces.component.export.PDFOrientationType;

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

    private static final String LOGO_PATH_PNG = "/images/logo.png";
    private static final String LOGO_PATH_JPG = "/images/logo.jpg";

    private ExcelOptions excelOptions;

    private PDFOptions pdfOptions;

    public DefaultDataTableExporter() {
        excelOptions = new ExcelOptions();
        excelOptions.setAutoSizeColumn(true);
        excelOptions.setFacetBgColor("#DCDCDC");
        excelOptions.setFacetFontColor("#000000");
        excelOptions.setFacetFontStyle("BOLD");
        excelOptions.setFacetFontSize("9");
        excelOptions.setCellFontSize("9");
        excelOptions.setFontName("SansSerif");

        pdfOptions = new PDFOptions();
        pdfOptions.setFacetBgColor("#DCDCDC");
        pdfOptions.setFacetFontColor("#000000");
        pdfOptions.setFacetFontStyle("BOLD");
        pdfOptions.setFacetFontSize("9");
        pdfOptions.setCellFontSize("9");
        pdfOptions.setFontName("SansSerif");
        pdfOptions.setOrientation(PDFOrientationType.LANDSCAPE);
    }

    public void preProcessorPDF(Object document) throws IOException, BadElementException, DocumentException {
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

    public ExcelOptions getExcelOptions() {
        return excelOptions;
    }

    public void setExcelOptions(ExcelOptions excelOptions) {
        this.excelOptions = excelOptions;
    }

    public PDFOptions getPdfOptions() {
        return pdfOptions;
    }

    public void setPdfOptions(PDFOptions pdfOptions) {
        this.pdfOptions = pdfOptions;
    }

}
