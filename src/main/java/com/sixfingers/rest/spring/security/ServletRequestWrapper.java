package com.sixfingers.rest.spring.security;

import org.springframework.http.HttpHeaders;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class ServletRequestWrapper extends HttpServletRequestWrapper {

    private final static String AUTHENTICATION_BEARER_SCHEMA = "Bearer";

    private final String authToken;

    public ServletRequestWrapper(final HttpServletRequest request, final String authToken) {
        super(request);
        this.authToken = authToken;
    }

    /**
     * Override this method to override Authorization header
     * TODO: Override getHeaders as well
     *
     * @param name
     * @return
     */
    public String getHeader(final String name) {
        if (HttpHeaders.AUTHORIZATION.equals(name)) {
            return AUTHENTICATION_BEARER_SCHEMA + " " + authToken;
        } else {
            return super.getHeader(name);
        }
    }
}
