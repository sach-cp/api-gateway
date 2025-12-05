package com.api.gateway.response;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Setter
@Getter
public class UserLoginResponse {
    private String token;

    public UserLoginResponse(String token) {
        this.token = token;
    }
}
