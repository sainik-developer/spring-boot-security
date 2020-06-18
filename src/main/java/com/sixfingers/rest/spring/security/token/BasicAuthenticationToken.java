package com.sixfingers.rest.spring.security.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;

@Deprecated
@Getter
public final class BasicAuthenticationToken extends AbstractAuthenticationToken {

    private final String credentials;
    private final Object principal = null;

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>BasicAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     *
     * @param basicAuthString -- credentials
     */
    public BasicAuthenticationToken(final String basicAuthString) {
        super(null);
        this.credentials = basicAuthString;
        setAuthenticated(false);
    }

    public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this basicAuthString to trusted - use constructor which takes a GrantedAuthority list");
        }
        super.setAuthenticated(false);
    }
}