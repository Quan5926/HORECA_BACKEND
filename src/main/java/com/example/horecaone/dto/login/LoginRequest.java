// LoginRequest.java
package com.example.horecaone.dto.login;

import lombok.Data;

@Data
public class LoginRequest {
    private String identifier; // email hoặc số điện thoại
    private String password;
    private String recaptchaToken;
}