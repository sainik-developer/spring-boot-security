package com.sixfingers.rest.spring.security.filter;

import com.sixfingers.rest.spring.security.token.BasicAuthenticationToken;
import org.springframework.security.authentication.AuthenticationManager;

// TODO to be used
@Deprecated
public class BasicAuthFilter extends AuthenticationFilter {
    private final static String AUTHENTICATION_BASIC_SCHEMA = "Basic";

    public BasicAuthFilter(final AuthenticationManager authenticationManager) {
        super(authenticationManager, AUTHENTICATION_BASIC_SCHEMA, BasicAuthenticationToken::new,
                AuthenticationPropagator.INSTANCE);
    }
}
