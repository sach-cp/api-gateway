package com.api.gateway.service;


import com.api.gateway.dto.UserLoginRequestDto;
import com.api.gateway.dto.UserSignupRequestDto;
import com.api.gateway.model.User;
import com.api.gateway.response.UserLoginResponse;
import reactor.core.publisher.Mono;

public interface UsersService {

    Mono<UserLoginResponse> findUser(UserLoginRequestDto authRequest);
    Mono<String> save(UserSignupRequestDto user);
    Mono<User> findByUsername(String username);
}
