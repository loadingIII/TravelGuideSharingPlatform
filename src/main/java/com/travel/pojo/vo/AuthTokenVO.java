package com.travel.pojo.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class AuthTokenVO {
    private String accessToken;
    private String tokenType;
    private long expiresIn;
    private UserMeVO user;
}
