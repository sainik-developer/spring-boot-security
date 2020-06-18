package com.sixfingers.rest.spring.security;

import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

@Getter
public class AuthUserDetails implements UserDetails {

    private final String username;
    private final String password = null;
    private final boolean accountNonExpired = true;
    private final boolean accountNonLocked = true;
    private final boolean credentialsNonExpired = true;
    private final boolean enabled = true;
    private final Collection<? extends GrantedAuthority> authorities = null;

    public AuthUserDetails(final String username) {
        this.username = username;
    }
}
