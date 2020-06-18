package com.sixfingers.rest.spring.security.filter;

import com.sixfingers.rest.spring.security.ServletRequestWrapper;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;

import javax.servlet.http.HttpServletRequest;
import java.util.function.BiFunction;

/***
 * This is to help later accommodate JWT parallel to redis bearer token
 */
public enum AuthenticationPropagator implements BiFunction<HttpServletRequest, Authentication, HttpServletRequest> {

    /***
     * This is a way to make a singleton and it's a thread safe
     */
    INSTANCE;

    @Override
    public HttpServletRequest apply(final HttpServletRequest request, final Authentication authentication) {
        HttpServletRequest forwardRequest = request;
        if (null != authentication.getCredentials()) {
            forwardRequest = new ServletRequestWrapper(request, (String) authentication.getCredentials());
            // Propagate to authorization to attribute as well to be picked up by other components that may only have a
            // handle to the original request object
            forwardRequest.setAttribute(HttpHeaders.AUTHORIZATION, forwardRequest.getHeader(HttpHeaders.AUTHORIZATION));
        }
        return forwardRequest;
    }
}
