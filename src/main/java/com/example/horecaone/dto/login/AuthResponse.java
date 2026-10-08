package com.example.horecaone.dto.login;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor  // Tự động tạo constructor với tất cả các field
public class AuthResponse {
    private String token;
    private String username;
    private String role;
    private Long id;
}