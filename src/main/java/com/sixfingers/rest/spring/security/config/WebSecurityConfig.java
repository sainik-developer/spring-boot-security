package com.sixfingers.rest.spring.security.config;

import com.sixfingers.rest.spring.security.filter.BearerAuthFilter;
import com.sixfingers.rest.spring.security.filter.NoAuthenticationEntryPoint;
import com.sixfingers.rest.spring.security.filter.UsernameAndPasswordAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.List;

/**
 * Base class for web security.
 * <p>
 * This class sets adds the authentication providers, which get called in Filters to authenticate
 * based on basic auth or jwt tokens. It also sets the unauthorized entry point and other default security
 * settings
 * <p>
 * {@see BearerAuthFilter}
 * {@see UsernamePasswordAuthenticationToken}
 */
@Configuration
@EnableWebSecurity
@ComponentScan(basePackages = "com.askfast.rest.spring.security")
@EnableGlobalMethodSecurity(securedEnabled = true, prePostEnabled = true)
@RequiredArgsConstructor
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    private final List<AuthenticationProvider> authenticationProviders;
    private final Environment environment;
    private final NoAuthenticationEntryPoint unauthorizedHandler;
    private final String DEFAULT_SECURED_PATH_PATTERN = "/api/**";
    private final String PROPERTY_SECURED_PATH_PATTERN = "core.rest.framework.security.secured-path-pattern";

    @Override
    @Bean
    public AuthenticationManager authenticationManagerBean() throws Exception {
        return super.authenticationManagerBean();
    }

    @Override
    public void configure(final AuthenticationManagerBuilder builder) throws Exception {
        authenticationProviders.forEach(builder::authenticationProvider);
    }

    /**
     * Important: Don't add @Component to BearerAuthFilter or declare it as @Bean in your project, otherwise Spring
     * boot will register this filter again. We are already registering this filter into the Spring security filter
     * chain below
     */
    @SuppressWarnings("JavadocReference")
    private BearerAuthFilter getBearerAuthFilter() throws Exception {
        return new BearerAuthFilter(authenticationManagerBean());
    }

//    /**
//     * Important: Don't add @Component to BasicAuthFilter or declare it as @Bean in your project, otherwise Spring
//     * boot will register this filter again. We are already registering this filter into the Spring security filter
//     * chain below
//     *
//     * @see docs.spring.io/spring-boot/docs/current/reference/htmlsingle/
//     * #howto-disable-registration-of-a-servlet-or-filter
//     */
//    @SuppressWarnings("JavadocReference")
//    private BasicAuthFilter getBasicAuthFilter() throws Exception {
//        return new BasicAuthFilter(authenticationManagerBean());
//    }

    private UsernamePasswordAuthenticationFilter getUsernamePasswordFilter() throws Exception {
        final UsernamePasswordAuthenticationFilter authenticationFilter = new UsernamePasswordAuthenticationFilter();
        authenticationFilter.setAuthenticationManager(authenticationManagerBean());
        return authenticationFilter;
    }

    @Override
    public void configure(final WebSecurity webSecurity) throws Exception {
        // note: put ignores here
    }

    @Override
    protected void configure(final HttpSecurity httpSecurity) throws Exception {
        final String securedPath = environment.getProperty(PROPERTY_SECURED_PATH_PATTERN, DEFAULT_SECURED_PATH_PATTERN);

        httpSecurity
                .anonymous().disable()
                .csrf().disable()
                .rememberMe().disable()
                .requestCache().disable()

                .formLogin()
                .loginProcessingUrl("/login/post") //the URL on which the clients should post the login information
                .usernameParameter("username") //the username parameter in the queryString, default is 'username'
                .passwordParameter("password")
                .successHandler(new UsernameAndPasswordAuthenticationSuccessHandler())
                .and()

                .exceptionHandling()
                .authenticationEntryPoint(unauthorizedHandler)
                .and()

                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()

                .authorizeRequests()
                .antMatchers(securedPath)
                .authenticated();

        // Filter for access token with User Auth service is the one that will run last
        httpSecurity.addFilterBefore(getBearerAuthFilter(), UsernamePasswordAuthenticationFilter.class);
        // TODO for later httpSecurity.addFilterBefore(getBasicAuthFilter(), UsernamePasswordAuthenticationFilter.class);
    }

}
