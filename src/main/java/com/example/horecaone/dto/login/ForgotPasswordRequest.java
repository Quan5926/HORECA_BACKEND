package com.example.horecaone.dto.login;

import lombok.Data;

@Data
public class ForgotPasswordRequest {
    private String email;
    private String otp;
    private String newPassword;
    private String resetToken;
    private String recaptchaToken;
}