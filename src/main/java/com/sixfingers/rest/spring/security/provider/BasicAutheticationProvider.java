package com.sixfingers.rest.spring.security.provider;

import com.sixfingers.rest.spring.security.token.BasicAuthenticationToken;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

/***
 *
 */
@Deprecated // TODO to be used
public class BasicAutheticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return null;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return BasicAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
