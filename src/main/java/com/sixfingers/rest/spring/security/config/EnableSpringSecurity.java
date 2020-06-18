package com.sixfingers.rest.spring.security.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation to enable spring security filter.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
@Documented
@Configuration
@Import(WebSecurityConfig.class)
public @interface EnableSpringSecurity {
}
