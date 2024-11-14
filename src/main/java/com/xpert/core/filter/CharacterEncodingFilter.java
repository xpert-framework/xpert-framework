package com.xpert.core.filter;

import java.io.IOException;
import java.util.logging.Logger;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import java.io.Serializable;

/**
 * Filter to set request and response to UTF-8 encoding.
 *
 * request.setCharacterEncoding(UTF-8); response.setCharacterEncoding(UTF-8);
 *
 * @author ayslan
 */
public class CharacterEncodingFilter implements Filter, Serializable {

    private static final long serialVersionUID = -3883620855631854552L;
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
