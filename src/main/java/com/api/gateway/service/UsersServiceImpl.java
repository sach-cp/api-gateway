package com.api.gateway.service;

import com.api.gateway.dto.UserLoginRequestDto;
import com.api.gateway.dto.UserSignupRequestDto;
import com.api.gateway.model.User;
import com.api.gateway.repo.UserRepository;
import com.api.gateway.response.UserLoginResponse;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;


@Slf4j
@Service
@AllArgsConstructor
public class UsersServiceImpl implements UsersService {

    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;

    @Override
    public Mono<UserLoginResponse> findUser(UserLoginRequestDto authRequest) {
        log.info("Searching for user: {}", authRequest.getUsername());
        Mono<User> userMono = userRepository.findByUsername(authRequest.getUsername());

        return userMono.flatMap(user -> {
                    if (passwordEncoder.matches(authRequest.getPassword(), user.getPassword())) {
                        String token = jwtService.generateToken(authRequest.getUsername());
                        return Mono.just(new UserLoginResponse(token));
                    } else {
                        return Mono.error(new BadCredentialsException("Invalid username or password"));
                    }
                })
                .switchIfEmpty(Mono.error(new BadCredentialsException("Invalid username or password")));
    }

    @Override
    public Mono<String> save(UserSignupRequestDto userSignupRequest) {
        User user = User.builder()
                .fullName(userSignupRequest.getFullName())
                .emailId(userSignupRequest.getEmailId())
                .username(generateUsernameFromEmail(userSignupRequest.getEmailId()))
                .build();
        user.setPassword(passwordEncoder.encode(userSignupRequest.getPassword())); // Encrypt password before saving
        Mono<User> savedUser = userRepository.save(user);
        log.info("User signed up: {}", user.getUsername());
        return savedUser.map(usr -> "User " + usr.getFullName() + " signed up successfully");
    }

    private String generateUsernameFromEmail(String email) {
        return email.split("@")[0].toLowerCase();
    }

    @Override
    public Mono<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
