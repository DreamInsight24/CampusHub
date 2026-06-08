package com.campushub.vo.user;

import java.util.UUID;

public class LoginVO {

    private String token;
    private UUID userUuid;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UUID getUserUuid() {
        return userUuid;
    }

    public void setUserUuid(UUID userUuid) {
        this.userUuid = userUuid;
    }
}
