package com.sixfingers.rest.spring.security.provider;

import com.sixfingers.rest.spring.security.AuthUserDetails;
import com.sixfingers.rest.spring.security.token.BearerAuthenticationToken;
import lombok.AllArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/***
 * This is the redis bearer token verification provider, It check if token is presents in redis if so then
 * it authenticate the call and allows the request to go to request end point
 */
@Log4j2
@Component
@AllArgsConstructor
public class BearerAuthenticationProvider implements AuthenticationProvider {

    private final StringRedisTemplate redisTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return Optional.of(authentication instanceof BearerAuthenticationToken)
                .filter(Boolean::booleanValue)
                .map(aBoolean -> redisTemplate.opsForValue().get(authentication.getCredentials()))
                .map(username -> {
                    redisTemplate.opsForValue().set((String) authentication.getCredentials(), username, 1, TimeUnit.HOURS);
                    return username;
                })
                .map(username -> new BearerAuthenticationToken(new AuthUserDetails(username), (String) authentication.getCredentials(), null))
                .orElse(null);
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return BearerAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
