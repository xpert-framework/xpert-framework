package com.xpert.faces.component.pdfprinter;

import org.htmlcleaner.CleanerProperties;
import org.htmlcleaner.HtmlCleaner;
import org.htmlcleaner.SimpleXmlSerializer;
import org.htmlcleaner.TagNode;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Entities;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

/**
 *
 * Utility class to normalize HTML. A well formed HTML must be with tags closed.
 *
 * @author Ayslan
 */
public class HtmlNormalizer {

    /**
     * Format HTML tags as XML, example: br, img, hr will be closed
     *
     * @param html
     * @return
     */
    public static String getFromHtmlCleaner(String html) {
        HtmlCleaner cleaner = new HtmlCleaner();
        CleanerProperties props = cleaner.getProperties();
        props.setAllowHtmlInsideAttributes(true);
        props.setAllowMultiWordAttributes(true);
        props.setRecognizeUnicodeChars(true);
        props.setOmitComments(true);

        TagNode node = cleaner.clean(html);
        SimpleXmlSerializer serializer = new SimpleXmlSerializer(cleaner.getProperties());

        return serializer.getAsString(node);
    }

    /**
     * Normalize the document, setting absolute link on images, css, etc. Set
     * display block on SVG.
     *
     * @param html
     * @param baseUri
     * @param pageOrientation
     * @return
     */
    public static String normalize(String html, String baseUri, PageOrientation pageOrientation) {

        html = getFromHtmlCleaner(html);

        Document document = Jsoup.parse(html, baseUri, Parser.xmlParser());
        document.outputSettings().escapeMode(Entities.EscapeMode.xhtml);

        document.select("script").remove();
        Elements elements = document.select("link,a");
        for (Element element : elements) {
            element.attr("href", element.absUrl("href"));
        }
        elements = document.select("link");
        for (Element element : elements) {
            element.attr("media", "all");
        }
        elements = document.select("script,img");
        for (Element element : elements) {
            element.attr("src", element.absUrl("src"));
        }
        //normalize svg, add display block
        elements = document.select("svg");
        for (Element element : elements) {
            String style = element.attr("style");
            String width = element.attr("width");
            String height = element.attr("height");
            if (style == null) {
                style = "";
            }
            if (style.endsWith(";")) {
                style = style + ";";
            }

            style = style + " display: block;";
            if (width != null && !width.isEmpty()) {
                style = style + " width: " + width + "px;";
            }
            if (height != null && !height.isEmpty()) {
                style = style + " height: " + height + "px;";
            }
            element.attr("style", style);
        }

        //put all styles in <head>
        Elements headSeletor = document.select("head");
        if (headSeletor != null && !headSeletor.isEmpty()) {
            //page orientation
            if (pageOrientation != null && pageOrientation.equals(PageOrientation.LANDSCAPE)) {
                headSeletor.append("<style media=\"print\" >@page {size: landscape}</style>");
            }
            //find style from body
            Elements stylesInBody = document.body().select("style");
            //append style from body to head
            headSeletor.append(stylesInBody.outerHtml());
            //remove style from body
            stylesInBody.remove();
        }

        //clippath
        Elements clipPaths = document.select("clippath");
        for (Element element : clipPaths) {
            element.tagName("clipPath");
        }

        //remove cli-path attribute because batik throws a error
        //tag comes with: <g clip-path="url(http://localhost:8080/xpert-showcase-war/views/components/pdfPrinter.jsf#_ABSTRACT_RENDERER_ID_0)">
        document.select("svg *").removeAttr("clip-path");

        //SVG clipath comes in lowerCase
        String documentHTML = document.html();
        documentHTML = documentHTML.replace("<clippath", "<clipPath").replace("</clippath", "</clipPath");

        return documentHTML;
    }

}
