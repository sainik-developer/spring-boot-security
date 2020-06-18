package com.sixfingers.rest.spring.security.token;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

/****
 *
 */
@Getter
public final class BearerAuthenticationToken extends AbstractAuthenticationToken {

    private final String credentials;
    private final UserDetails principal;

    /**
     * This constructor can be safely used by any code that wishes to create a
     * <code>BearerAuthenticationToken</code>, as the {@link #isAuthenticated()}
     * will return <code>false</code>.
     *
     * @param token -- credentials
     */
    public BearerAuthenticationToken(final String token) {
        super(null);
        this.credentials = token;
        this.principal = null;
        setAuthenticated(false);
    }

    /**
     * This constructor should only be used by <code>AuthenticationManager</code> or
     * <code>AuthenticationProvider</code> implementations that are satisfied with
     * producing a trusted (i.e. {@link #isAuthenticated()} = <code>true</code>)
     * authentication token.
     *
     * @param userDetails -- principal
     * @param token       -- credentials
     * @param authorities -- granted authorities
     */
    public BearerAuthenticationToken(final UserDetails userDetails, final String token,
                                     final Collection<? extends GrantedAuthority> authorities) {
        super(authorities);
        this.principal = userDetails;
        this.credentials = token;
        super.setAuthenticated(true); // must use super, as we override
    }

    public void setAuthenticated(final boolean isAuthenticated) throws IllegalArgumentException {
        if (isAuthenticated) {
            throw new IllegalArgumentException(
                    "Cannot set this token to trusted - use constructor which takes a GrantedAuthority list instead");
        }
        super.setAuthenticated(false);
    }
}
