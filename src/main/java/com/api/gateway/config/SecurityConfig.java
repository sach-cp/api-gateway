package com.api.gateway.config;

import lombok.NoArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;

@Configuration
@EnableWebFluxSecurity
@NoArgsConstructor
public class SecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         AuthenticationWebFilter jwtAuthenticationWebFilter) {
        return http.csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/api/v1/users/login", "/api/v1/users/signup").permitAll()
                        .pathMatchers(HttpMethod.OPTIONS).permitAll()
                        .anyExchange().authenticated())
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .build();
    }


    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter(JWTAuthenticationManager authManager,
                                                              JwtServerAuthenticationConverter converter) {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authManager);
        filter.setServerAuthenticationConverter(converter);
        return filter;
    }

}
