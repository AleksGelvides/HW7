package com.example.HW7.configuration;

import com.example.HW7.data.AppUserPrincipal;
import com.example.HW7.exceptions.AppException;
import com.example.HW7.repo.entity.RoleType;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.authorization.ReactiveAuthorizationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authorization.AuthorizationContext;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfiguration {
    private final static String USER_ID_PARAM = "userId";
    private static final String[] SWAGGER_AUTH_WHITELIST = {
            // -- Swagger UI v2
            "/v2/api-docs",
            "/swagger-resources",
            "/swagger-resources/**",
            "/configuration/ui",
            "/configuration/security",
            "/swagger-ui.html",
            "/webjars/**",
            // -- Swagger UI v3 (OpenAPI)
            "/v3/api-docs/**",
            "/swagger-ui/**"
            // other public endpoints of your API may be appended to this array
    };

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(12);
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager(ReactiveUserDetailsService userDetailsService,
                                                               PasswordEncoder passwordEncoder) {
        var reactiveAuthenticationManager =
                new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);

        reactiveAuthenticationManager.setPasswordEncoder(passwordEncoder);

        return reactiveAuthenticationManager;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity httpSecurity,
                                                         ReactiveAuthenticationManager authenticationManager) {
        return httpSecurity.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange((auth) -> auth
                        .pathMatchers(HttpMethod.POST, "api/v1/users/**").permitAll()
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/users/**").access(userAuthManager())
                        .pathMatchers(HttpMethod.PUT, "/api/v1/users/**").access(userAuthManager())
                        .pathMatchers(SWAGGER_AUTH_WHITELIST).permitAll()
                        .anyExchange().authenticated())
                .httpBasic(Customizer.withDefaults())
                .authenticationManager(authenticationManager)
                .build();
    }

    @Bean
    public ReactiveAuthorizationManager<AuthorizationContext> userAuthManager() {
        return new ReactiveAuthorizationManager<>() {
            @Override
            public Mono<AuthorizationDecision> check(Mono<Authentication> authentication, AuthorizationContext object) {
                return authentication
                        .map(Authentication::getPrincipal)
                        .map(principal -> (AppUserPrincipal) principal)
                        .map(principal -> {
                            String anotherUserId = getQueryParam(object.getExchange(), USER_ID_PARAM);
                            if (!principal.getRoles().contains(RoleType.ROLE_MANAGER) &&
                                    !principal.getId().equals(anotherUserId)) {
                                return new AuthorizationDecision(false);
                            }
                                return new AuthorizationDecision(true);
                        });
            }
        };
    }

    private String getQueryParam(ServerWebExchange exchange, String param) {
        try {
            return exchange.getRequest().getQueryParams().get(param).get(0);
        } catch (NullPointerException e) {
            return null;
        }
    }
}
