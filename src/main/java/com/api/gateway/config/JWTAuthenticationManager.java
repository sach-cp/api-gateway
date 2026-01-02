package com.api.gateway.config;

import com.api.gateway.service.JwtService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.util.List;

@Component
public class JWTAuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;

    public JWTAuthenticationManager(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String token = authentication.getCredentials().toString();
        if (!jwtService.isTokenValid(token)) {
            return Mono.error(new BadCredentialsException("Invalid JWT token"));
        }

        String username = jwtService.extractUsername(token);
        return Mono.just(new UsernamePasswordAuthenticationToken(username, null, List.of()));
    }

}
