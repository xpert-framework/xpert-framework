package com.xpert.core.filter;

import java.io.IOException;
import java.util.logging.Logger;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

/**
 * Filter to set request and response to UTF-8 encoding.
 * 
 * request.setCharacterEncoding(UTF-8);
 * response.setCharacterEncoding(UTF-8);
 * 
 * @author Ayslan
 */
public class CharacterEncodingFilter implements Filter {

    private static final Logger logger = Logger.getLogger(CharacterEncodingFilter.class.getName());
    private static final String UTF_8 = "UTF-8";

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        request.setCharacterEncoding(UTF_8);
        response.setCharacterEncoding(UTF_8);
        chain.doFilter(request, response);
    }

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("Using com.xpert.core.filter.CharacterEncodingFilter to encode requests to " + UTF_8);
    }

    @Override
    public void destroy() {
    }
}
