package com.xpert.faces.component.pdfprinter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextReplacedElementFactory;
import org.xhtmlrenderer.render.BlockBox;

/**
 *
 * @author ayslan
 */
public class SVGReplacedElementFactory extends ITextReplacedElementFactory {

    public SVGReplacedElementFactory(ITextOutputDevice outputDevice) {
        super(outputDevice);
    }

    @Override
    public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box,
            UserAgentCallback uac, int cssWidth, int cssHeight) {
        Element element = box.getElement();
        String nodeName = element.getNodeName();
        if (nodeName.equals("img")) {
            return super.createReplacedElement(c, box, uac, cssWidth, cssHeight);
        } else if (nodeName.equals("svg")) {
            DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilderFactory.setNamespaceAware(true);
            DocumentBuilder documentBuilder;

            try {
                documentBuilder = documentBuilderFactory.newDocumentBuilder();
            } catch (ParserConfigurationException e) {
                throw new RuntimeException(e);
            }
            Document svgDocument = documentBuilder.newDocument();

            Element svgElement = (Element) svgDocument.importNode(element, true);

            //hack to namespace
            svgElement.setAttributeNS("http://www.w3.org/2000/svg", "svg", null);
            svgDocument.appendChild(svgElement);

            return new SVGReplacedElement(svgDocument, cssWidth, cssHeight);
        }
        return null;
    }

}
