package com.xpert.faces.utils;

import com.xpert.jasper.JRBeanCollectionDataSource;
import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.persistence.EntityManager;
import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.export.JExcelApiExporterParameter;
import net.sf.jasperreports.engine.export.ooxml.JRXlsxExporter;

/**
 * Generic class to create Jasper Reports
 *
 * @author Ayslan
 */
public class FacesJasper {

    private static final Logger logger = Logger.getLogger(FacesJasper.class.getName());

    public static JasperPrint fillReport(List dataSource, Map parameters, String path) throws JRException {
        return fillReport(dataSource, parameters, path, null);
    }

    public static JasperPrint fillReport(List dataSource, Map parameters, String path, EntityManager entityManager) throws JRException {
        if (dataSource == null || dataSource.isEmpty()) {
            JREmptyDataSource jREmptyDataSource = new JREmptyDataSource();
            return JasperFillManager.fillReport(path, parameters, jREmptyDataSource);
        } else {
            JRDataSource jRDataSource = null;
            if (entityManager != null) {
                jRDataSource = new JRBeanCollectionDataSource(dataSource, entityManager);
            } else {
                jRDataSource = new net.sf.jasperreports.engine.data.JRBeanCollectionDataSource(dataSource);
            }
            return JasperFillManager.fillReport(path, parameters, jRDataSource);
        }
    }

    /**
     * Create a jasper report
     *
     * @param dataSource DataSource of report
     * @param parameters Map of parameters
     * @param path Path of Report
     * @param fileName Name of generated file
     */
    public static void createJasperReport(List dataSource, Map parameters, String path, String fileName) {
        createJasperReport(dataSource, parameters, path, fileName, true);
    }

    /**
     * Create a jasper report
     *
     * @param dataSource DataSource of report
     * @param parameters Map of parameters
     * @param path Path of Report
     * @param fileName Name of generated file
     * @param attachment indicates attachment in header
     */
    public static void createJasperReport(List dataSource, Map parameters, String path, String fileName, boolean attachment) {
        createJasperReport(dataSource, parameters, path, fileName, null, attachment);
    }

    /**
     * Create a jasper report
     *
     * @param dataSource DataSource of report
     * @param parameters Map of parameters
     * @param path Path of Report
     * @param fileName Name of generated file
     * @param entityManager
     */
    public static void createJasperReport(List dataSource, Map parameters, String path, String fileName, EntityManager entityManager) {
        createJasperReport(dataSource, parameters, path, fileName, entityManager, true);
    }

    /**
     * Create a jasper report
     *
     * @param dataSource DataSource of report
     * @param parameters Map of parameters
     * @param path Path of Report
     * @param fileName Name of generated file
     * @param entityManager
     * @param attachment indicates attachment in header
     */
    public static void createJasperReport(List dataSource, Map parameters, String path, String fileName, EntityManager entityManager, boolean attachment) {

        try {
            String layout = FacesContext.getCurrentInstance().getExternalContext().getRealPath(path);
            JasperPrint jasperPrint = fillReport(dataSource, parameters, layout, entityManager);
            FacesUtils.download(JasperExportManager.exportReportToPdf(jasperPrint), "application/pdf", fileName.endsWith(".pdf") ? fileName : fileName + ".pdf", attachment);
        } catch (JRException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }

    /**
     * Create a jasper report with xlsx format
     *
     * @param dataSource DataSource of report
     * @param parameters Map of parameters
     * @param path Path of Report
     * @param fileName Name of generated file
     */
    public static void createJasperExcel(List dataSource, Map parameters, String path, String fileName) {
        createJasperExcel(dataSource, parameters, path, fileName, null);
    }

    /**
     * Create a jasper report with xlsx format
     *
     * @param dataSource DataSource of report
     * @param parameters Map of parameters
     * @param path Path of Report
     * @param fileName Name of generated file
     * @param entityManager
     */
    public static void createJasperExcel(List dataSource, Map parameters, String path, String fileName, EntityManager entityManager) {

        try {
            String layout = FacesContext.getCurrentInstance().getExternalContext().getRealPath(path);
            JasperPrint jasperPrint = fillReport(dataSource, parameters, layout, entityManager);

            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();

            JRXlsxExporter xlsxExporter = new JRXlsxExporter();
            xlsxExporter.setParameter(JExcelApiExporterParameter.JASPER_PRINT, jasperPrint);
            xlsxExporter.setParameter(JExcelApiExporterParameter.OUTPUT_STREAM, outputStream);
            xlsxExporter.setParameter(JExcelApiExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.TRUE);
            xlsxExporter.setParameter(JExcelApiExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.TRUE);
            xlsxExporter.setParameter(JExcelApiExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            xlsxExporter.setParameter(JExcelApiExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.FALSE);
            xlsxExporter.setParameter(JExcelApiExporterParameter.CHARACTER_ENCODING, "UTF-8");
            xlsxExporter.exportReport();

            FacesUtils.download(outputStream.toByteArray(), "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet", fileName.endsWith(".xlsx") ? fileName : fileName + ".xlsx");
        } catch (JRException ex) {
            logger.log(Level.SEVERE, null, ex);
            throw new RuntimeException(ex);
        }
    }
}
