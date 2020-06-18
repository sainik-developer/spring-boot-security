package com.sixfingers.rest.spring.security.provider;

import com.sixfingers.rest.spring.security.AuthUserDetails;
import com.sixfingers.rest.spring.security.service.AccountService;
import com.sixfingers.rest.spring.security.token.BearerAuthenticationToken;
import com.sixfingers.rest.spring.security.utils.Utils;
import lombok.AllArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

/***
 * If user does not have Bearer token, it will try to see if user tries to authenticate
 * the call if it's a `POST` `/login` call with form parameter named 'username' and 'password'
 * it searches on mongo db and
 */
@Component
@AllArgsConstructor
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

    private final AccountService accountService;
    private final StringRedisTemplate redisTemplate;

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        return Optional.of(authentication instanceof UsernamePasswordAuthenticationToken)
                .filter(Boolean::booleanValue)
                .flatMap(s -> accountService.getByUsernameAndPassword((String) authentication.getPrincipal(), (String) authentication.getCredentials()))
                .map(account -> {
                    final String key = Utils.generateRandomHash(); // TODO we have to try at least three times
                    // keeping the key alive for 1 hours
                    redisTemplate.opsForValue().set(key, account.getId(), 1L, TimeUnit.HOURS);
                    return new BearerAuthenticationToken(new AuthUserDetails(account.getUserName()), key, null);
                })
                .orElseThrow(() -> new BadCredentialsException("auth failed"));
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return UsernamePasswordAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
