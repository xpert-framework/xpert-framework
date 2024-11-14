package com.xpert.faces.component.pdfprinter;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.IOUtils;
import org.xhtmlrenderer.pdf.ITextOutputDevice;
import org.xhtmlrenderer.pdf.ITextUserAgent;
import org.xhtmlrenderer.resource.CSSResource;

/**
 *
 * @author ayslan
 */
public class CustomUserAgentCallback extends ITextUserAgent implements Serializable {

    private static final long serialVersionUID = 2102336950486828764L;
    
    private static final Logger logger = Logger.getLogger(CustomUserAgentCallback.class.getName());

    private static final Map<String, byte[]> CACHE = new HashMap<>();

    private boolean loadFromCache = true;
    private boolean replaceHttps = true;

    public CustomUserAgentCallback(ITextOutputDevice outputDevice) {
        super(outputDevice, 72);
    }
    
    public static void clearCache() {
        CACHE.clear();
    }

    @Override
    public CSSResource getCSSResource(String uri) {
        byte[] resource = null;
       
        if (replaceHttps && uri.startsWith("https://")) {
            uri = uri.replace("https://", "http://");
        }

        if (loadFromCache == true) {
            resource = CACHE.get(uri);
        }
        try {
            if (resource == null || resource.length == 0) {

                InputStream inputStream = resolveAndOpenStream(uri);
                if (inputStream != null) {
                    resource = IOUtils.toByteArray(inputStream);
                    CACHE.put(uri, resource);
                }
            }
            if (resource != null && resource.length > 0) {
                return new CSSResource(new ByteArrayInputStream(resource));
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public boolean isLoadFromCache() {
        return loadFromCache;
    }

    public void setLoadFromCache(boolean loadFromCache) {
        this.loadFromCache = loadFromCache;
    }

    public boolean isReplaceHttps() {
        return replaceHttps;
    }

    public void setReplaceHttps(boolean replaceHttps) {
        this.replaceHttps = replaceHttps;
    }

}
