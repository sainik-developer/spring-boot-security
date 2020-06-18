package com.sixfingers.rest.spring.security.filter;

import com.sixfingers.rest.spring.security.utils.Utils;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Optional;
import java.util.function.BiFunction;
import java.util.function.Function;

@Log4j2
@AllArgsConstructor
public abstract class AuthenticationFilter extends OncePerRequestFilter {

    private final AuthenticationManager authenticationManager;
    private final String authenticationType;
    private final Function<String, ? extends Authentication> tokenGenerator;
    private final BiFunction<HttpServletRequest, Authentication, HttpServletRequest> handleAuthResult;

    /**
     * {@inheritDoc}
     */
    @Override
    protected void doFilterInternal(final HttpServletRequest request,
                                    final HttpServletResponse response,
                                    final FilterChain filterChain) throws ServletException, IOException {
        // By default, we will forward the incoming request
        HttpServletRequest forwardedRequest = request;
        final String authorization = getToken(request);
        // as for Username and password it authorization will be null
        if (authorization != null && authorization.startsWith(authenticationType)) {

            // Strip authenticationType from string. Do it so auth tokens are consistently passed authorization as credentials
            final String token = authorization.substring(authenticationType.length()).trim();
            final Authentication authentication = tokenGenerator.apply(token);
            try {
                // Allow implementations the opportunity to act on the authentication result
                // This is to help later accommodate JWT bearer authentication which is required and more efficient for internal micro-service call
                forwardedRequest = handleAuthResult.apply(request, authenticationManager.authenticate(authentication));
            } catch (final Exception e) {
                // No need to throw exception here. Let normal filter process continue since not having authenticated
                // result in security context will return appropriate result to client
                log.warn("Unable to authenticate for Schema: {}", authenticationType, e);
            }
        }
        filterChain.doFilter(forwardedRequest, response);
    }

    private String getToken(final HttpServletRequest httpServletRequest) {
        if (httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION) != null) {
            return httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
        } else if (httpServletRequest.getHeader(Utils.X_SESSION_ID) != null) {
            return httpServletRequest.getHeader(Utils.X_SESSION_ID);
        } else if (httpServletRequest.getCookies() != null) {
            getTokenFromCookie(httpServletRequest.getCookies()[0]);
        }
        return httpServletRequest.getHeader(HttpHeaders.AUTHORIZATION);
    }

    private String getTokenFromCookie(final Cookie cookie) {
        return Optional.ofNullable(cookie)
                .map(c -> c.getValue().startsWith(Utils.X_SESSION_ID))
                .filter(Boolean::booleanValue)
                .map(aBoolean -> cookie.getValue().substring(cookie.getValue().indexOf('=')))
                .orElse(null);
    }
}
