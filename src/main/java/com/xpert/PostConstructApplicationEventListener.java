package com.xpert;

import com.xpert.faces.utils.FacesUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.SystemEvent;
import javax.faces.event.SystemEventListener;

public class PostConstructApplicationEventListener implements SystemEventListener {
    
    private final static Logger logger = Logger.getLogger(PostConstructApplicationEventListener.class.getName());
    
    public boolean isListenerForSource(Object source) {
        return true;
    }
    
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        logger.log(Level.INFO, "Running on Xpert-framework {0}", Constants.VERSION);
        Configuration.init(FacesUtils.getServletContext());
    }
}
