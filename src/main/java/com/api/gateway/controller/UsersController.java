package com.api.gateway.controller;

import com.api.gateway.dto.UserLoginRequestDto;
import com.api.gateway.dto.UserSignupRequestDto;
import com.api.gateway.response.UserLoginResponse;
import com.api.gateway.service.UsersService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("api/v1/users")
@AllArgsConstructor
public class UsersController {

    private final UsersService service;

    @PostMapping("/login")
    public Mono<ResponseEntity<UserLoginResponse>> login(@RequestBody UserLoginRequestDto authRequest) {
        return service.findUser(authRequest)
                .map(ResponseEntity::ok)
                .onErrorResume(BadCredentialsException.class,
                        e -> Mono.just(ResponseEntity.status(401).build()));
    }

    @PostMapping("/signup")
    public Mono<ResponseEntity<String>> signup(@RequestBody UserSignupRequestDto user) {
        return service.save(user).map(message -> ResponseEntity.ok().body(message));
    }
}
