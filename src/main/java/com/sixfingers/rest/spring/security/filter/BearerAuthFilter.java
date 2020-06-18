package com.sixfingers.rest.spring.security.filter;

import com.sixfingers.rest.spring.security.token.BearerAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import javax.servlet.http.HttpServletRequest;

/***
 *
 */
public class BearerAuthFilter extends AuthenticationFilter {
    private final static String AUTHENTICATION_BEARER_SCHEMA = "Bearer";

    public BearerAuthFilter(final AuthenticationManager authenticationManager) {
        super(authenticationManager, AUTHENTICATION_BEARER_SCHEMA, BearerAuthenticationToken::new,
                BearerAuthFilter::populateSecurityContext);
    }

    private static HttpServletRequest populateSecurityContext(final HttpServletRequest request,
                                                              final Authentication authentication) {
        if (authentication.isAuthenticated()) {
            // set authenticated object on the security context. AuthResult contains principal and authorities
            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        return request;
    }
}
