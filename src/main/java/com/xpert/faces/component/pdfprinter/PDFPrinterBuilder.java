package com.xpert.faces.component.pdfprinter;

import com.itextpdf.text.DocumentException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.xhtmlrenderer.layout.SharedContext;
import org.xhtmlrenderer.pdf.ITextRenderer;
import org.xhtmlrenderer.resource.FSEntityResolver;
import org.xml.sax.SAXException;

/**
 *
 * @author Ayslan
 */
public class PDFPrinterBuilder {

    public static boolean DEBUG = false;
    private static final String EMPTY_HTML = "<html><head></head><body></body></html>";
    private static final Logger logger = Logger.getLogger(PDFPrinterBuilder.class.getName());

    /**
     * Get base URI of application, the pattern is : scheme + server name+ port,
     * example: http://180.1.1.10:8080
     *
     * @param context
     * @return
     */
    public static String getBaseURI(FacesContext context) {
        HttpServletRequest request = (HttpServletRequest) context.getExternalContext().getRequest();
        return request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
    }

    /**
     * Return a byte representation of a PDF file, based on a HTML String. The
     * conversion is made using framework flying-saucer. Default page
     * orientation is "portrait"
     *
     * @param context
     * @param html
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static byte[] createPDF(FacesContext context, String html) throws DocumentException, IOException {
        return createPDF(context, html, PageOrientation.PORTRAIT, true, true);
    }

    /**
     * Return a byte representation of a PDF file, based on a HTML String. The
     * conversion is made using framework flying-saucer.
     *
     * @param context
     * @param html
     * @param pageOrientation
     * @param cacheCss Cache CSS resources
     * @param replaceHttp Replace HTTPS with HTTP
     * @return
     * @throws DocumentException
     * @throws IOException
     */
    public static byte[] createPDF(FacesContext context, String html, PageOrientation pageOrientation, boolean cacheCss, boolean replaceHttp) throws DocumentException, IOException {

        if (html == null || html.trim().isEmpty()) {
            html = EMPTY_HTML;
        }

        long inicio = System.currentTimeMillis();

        String content = HtmlNormalizer.normalize(html, getBaseURI(context), pageOrientation);

        long fim = System.currentTimeMillis();
        if (DEBUG) {
            logger.log(Level.INFO, "HTML normalized in {0}ms", (fim - inicio));
        }

        inicio = System.currentTimeMillis();
//        System.out.println(content);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        //XRLog.setLoggingEnabled(DEBUG);
        //create renderer
        try {

            ITextRenderer iTextRenderer = new ITextRenderer();
            //iTextRenderer.setDocumentFromString(content);
            ChainingReplacedElementFactory chainingReplacedElementFactory = new ChainingReplacedElementFactory();
            chainingReplacedElementFactory.addReplacedElementFactory(new SVGReplacedElementFactory(iTextRenderer.getOutputDevice()));
            SharedContext sharedContext = iTextRenderer.getSharedContext();
            sharedContext.setReplacedElementFactory(chainingReplacedElementFactory);
            sharedContext.setUserAgentCallback(new CustomUserAgentCallback(cacheCss, replaceHttp));
            iTextRenderer.setDocumentFromString(content);

            //to convert svg
            iTextRenderer.layout();

            //write
            iTextRenderer.createPDF(baos);
            baos.flush();
            baos.close();

            fim = System.currentTimeMillis();

            if (DEBUG) {
                logger.log(Level.INFO, "PDF created in {0}ms", (fim - inicio));
            }
            return baos.toByteArray();
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    public Document getDocument(String content) throws UnsupportedEncodingException, SAXException, IOException, ParserConfigurationException {
        final DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        documentBuilderFactory.setNamespaceAware(false);
        documentBuilderFactory.setValidating(false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/namespaces", false);
        documentBuilderFactory.setFeature("http://xml.org/sax/features/validation", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
        documentBuilderFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        documentBuilderFactory.setValidating(false);
        DocumentBuilder builder = documentBuilderFactory.newDocumentBuilder();
        builder.setEntityResolver(FSEntityResolver.instance());
        return builder.parse(new ByteArrayInputStream(content.getBytes("UTF-8")));
    }

}
