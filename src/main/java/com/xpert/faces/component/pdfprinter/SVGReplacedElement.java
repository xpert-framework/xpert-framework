package com.xpert.faces.component.pdfprinter;

import com.itextpdf.awt.PdfGraphics2D;
import java.awt.Graphics2D;
import java.awt.Point;
import org.w3c.dom.Document;
import org.xhtmlrenderer.css.style.CalculatedStyle;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextReplacedElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.PageBox;
import org.xhtmlrenderer.render.RenderingContext;

import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfTemplate;
import com.lowagie.text.PageSize;
import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.io.StringReader;
import java.io.StringWriter;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.print.PrintTranscoder;

/**
 *
 * @author ayslan
 */
public class SVGReplacedElement implements ITextReplacedElement {

    private Point location = new Point(0, 0);
    private Document svg;
    private int cssWidth;
    private int cssHeight;

    public SVGReplacedElement(Document svg, int cssWidth, int cssHeight) {
        this.cssWidth = cssWidth;
        this.cssHeight = cssHeight;
        this.svg = svg;
    }

    @Override
    public void detach(LayoutContext c) {
    }

    @Override
    public int getBaseline() {
        return 0;
    }

    @Override
    public int getIntrinsicWidth() {
        return cssWidth;
    }

    @Override
    public int getIntrinsicHeight() {
        return cssHeight;
    }

    @Override
    public boolean hasBaseline() {
        return false;
    }

    @Override
    public boolean isRequiresInteractivePaint() {
        return false;
    }

    @Override
    public Point getLocation() {
        return location;
    }

    @Override
    public void setLocation(int x, int y) {
        this.location.x = x;
        this.location.y = y;
    }

    public static String toString(Document doc) {
        try {
            StringWriter sw = new StringWriter();
            TransformerFactory tf = TransformerFactory.newInstance();
            Transformer transformer = tf.newTransformer();
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            transformer.setOutputProperty(OutputKeys.METHOD, "xml");
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty(OutputKeys.STANDALONE, "yes");
            transformer.setOutputProperty(OutputKeys.ENCODING, "UTF-8");

            DOMSource source = new DOMSource(doc);
            transformer.transform(source, new StreamResult(sw));

            String result = sw.toString();
            result = result.replace("xmlns:ns0=\"http://www.w3.org/2000/svg\"", "").replace("ns0:svg=\"\"", "");
//            return result;
            return result.replace("<svg", "<svg xmlns=\"http://www.w3.org/2000/svg\" ");
        } catch (Exception ex) {
            throw new RuntimeException("Error converting to String", ex);
        }
    }

    @Override
    public void paint(RenderingContext renderingContext, ITextOutputDevice outputDevice,
            BlockBox blockBox) {

        PdfContentByte cb = outputDevice.getWriter().getDirectContent();
        float width = PageSize.A4.getWidth();//(float) (cssWidth / outputDevice.getDotsPerPoint());
        float height = (float) (cssHeight / outputDevice.getDotsPerPoint());
        PdfTemplate template = cb.createTemplate(width, height);

        Graphics2D g2d = new PdfGraphics2D(template, width, height);
        String svgString = toString(svg);

        PrintTranscoder prm = new PrintTranscoder();
        TranscoderInput ti = new TranscoderInput(new StringReader(svgString));
        prm.transcode(ti, null);
        PageFormat pg = new PageFormat();
        Paper pp = new Paper();
        pp.setSize(PageSize.A4.getWidth(), height);
        pp.setImageableArea(0, 0, width, height);
        pg.setPaper(pp);
        prm.print(g2d, pg, 0);
        g2d.dispose();

        PageBox page = renderingContext.getPage();
        float x = (float) blockBox.getAbsX() + page.getMarginBorderPadding(renderingContext, CalculatedStyle.LEFT);
        float y = (float) (page.getBottom() - (blockBox.getAbsY() + cssHeight)) + page.getMarginBorderPadding(
                renderingContext, CalculatedStyle.BOTTOM);
        x /= outputDevice.getDotsPerPoint();
        y /= outputDevice.getDotsPerPoint();

        cb.addTemplate(template, x, y);
    }
}
