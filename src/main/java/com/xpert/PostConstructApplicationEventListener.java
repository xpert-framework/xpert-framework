package com.xpert;

import com.xpert.faces.utils.FacesUtils;
import java.util.logging.Level;
import java.util.logging.Logger;
import jakarta.faces.event.AbortProcessingException;
import jakarta.faces.event.SystemEvent;
import jakarta.faces.event.SystemEventListener;
import java.io.Serializable;

public class PostConstructApplicationEventListener implements SystemEventListener, Serializable {
    
    private static final long serialVersionUID = -3397674125839340840L;
    
    private final static Logger logger = Logger.getLogger(PostConstructApplicationEventListener.class.getName());
    
    @Override
    public boolean isListenerForSource(Object source) {
        return true;
    }
    
    @Override
    public void processEvent(SystemEvent event) throws AbortProcessingException {
        logger.log(Level.INFO, "Running on Xpert-framework {0}", Constants.VERSION);
        Configuration.init(FacesUtils.getServletContext());
    }
}
