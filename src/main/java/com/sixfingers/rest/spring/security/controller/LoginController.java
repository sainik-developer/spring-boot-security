package com.sixfingers.rest.spring.security.controller;

import com.sixfingers.rest.spring.security.service.AccountService;
import com.sixfingers.rest.spring.security.utils.Utils;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;
import java.util.concurrent.TimeUnit;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final AccountService accountService;
    private final StringRedisTemplate redisTemplate;

    @RequestMapping(value = "/login", method = RequestMethod.GET)
    public ResponseEntity<Object> usernamePasswordLogin(@RequestParam("username") final String userName, @RequestParam("password") final String password) {
        return accountService.getByUsernameAndPassword(userName, password)
                .map(account -> {
                    final String key = Utils.generateRandomHash();
                    redisTemplate.opsForValue().set(key, account.getId(), 1L, TimeUnit.HOURS);
                    final HttpHeaders httpHeaders = new HttpHeaders();
                    httpHeaders.add(HttpHeaders.SET_COOKIE, Utils.X_SESSION_ID + "=" + "Bearer " + key);
                    httpHeaders.add(Utils.X_SESSION_ID, "Bearer " + key);
                    return ResponseEntity.ok().headers(httpHeaders).build();
                })
                .orElseThrow(() -> new BadCredentialsException("auth failed"));
    }

    @RequestMapping(value = "/keyserver/token", method = RequestMethod.POST)
    public ResponseEntity<Object> tokenAuth(@RequestParam("client_id") final String clientId,
                                            @RequestParam("grant_type") final String grantType,
                                            @RequestParam("refresh_token") final String refreshToken,
                                            @RequestParam("client_secret") final String clientSecret) {
        return Optional.of(grantType != null && grantType.equals("refresh_token"))
                .filter(Boolean::booleanValue)
                .flatMap(aBoolean -> accountService.getByToken(refreshToken))
                .map(refreshTokenObj -> {
                    if (refreshTokenObj.getRight().equals(clientId)) {
                        final String key = Utils.generateRandomHash();
                        redisTemplate.opsForValue().set(key, refreshTokenObj.getLeft(), 1L, TimeUnit.HOURS);
                        final HttpHeaders httpHeaders = new HttpHeaders();
                        httpHeaders.add(HttpHeaders.AUTHORIZATION, "Bearer " + key);
                        return ResponseEntity.ok().headers(httpHeaders).build();
                    } else {
                        return ResponseEntity.badRequest().build();
                    }
                })
                .orElseThrow(() -> new BadCredentialsException("auth failed"));
    }
}
