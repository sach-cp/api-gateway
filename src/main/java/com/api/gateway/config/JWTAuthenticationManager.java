package com.api.gateway.config;

import com.api.gateway.service.JwtService;
import com.api.gateway.service.UsersService;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

@Component
public class JWTAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;
    private final UsersService userService;

    public JWTAuthenticationManager(JwtService jwtService, UsersService userService) {
        this.jwtService = jwtService;
        this.userService = userService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) throws AuthenticationException {
        String token = authentication.getCredentials().toString();
        String username = jwtService.extractUsername(token);

        return userService.findByUsername(username)
                .map(userDetails -> {
                    if (jwtService.validateToken(token, userDetails.getUsername())) {
                        return authentication;
                    } else {
                        throw new AuthenticationException("Invalid JWT token") {
                        };
                    }
                });
    }

    public ServerAuthenticationConverter authenticationConverter() {
        return exchange -> {
            String token = exchange.getRequest().getHeaders().getFirst("Authorization");
            if (token != null && token.startsWith("Bearer ")) {
                return Mono.just(SecurityContextHolder.getContext().getAuthentication());
            }
            return Mono.empty();
        };
    }

}
