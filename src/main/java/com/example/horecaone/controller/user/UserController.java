package com.example.horecaone.controller.user;

import com.example.horecaone.dto.user.UserDTO;
import com.example.horecaone.exception.ResourceNotFoundException;
import com.example.horecaone.security.login.JwtUtil;
import com.example.horecaone.service.user.UserService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;
@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;
    private final JwtUtil jwtUtil;

    @GetMapping("/{id}")
    public ResponseEntity<UserDTO.UserProfileResponse> getProfile(@PathVariable Long id) {
        return ResponseEntity.ok(userService.getProfile(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserDTO.UserProfileResponse> updateProfile(
            @PathVariable Long id,
            @RequestBody UserDTO.UpdateProfileRequest request) {
        return ResponseEntity.ok(userService.updateProfile(id, request));
    }

    @PutMapping("/{id}/change-password")
    public ResponseEntity<UserDTO.MessageResponse> changePassword(
            @PathVariable Long id,
            @RequestBody UserDTO.ChangePasswordRequest request) {
        return ResponseEntity.ok(userService.changePassword(id, request));
    }

    @PutMapping("/{id}/avatar")
    public ResponseEntity<UserDTO.UserProfileResponse> updateAvatar(
            @PathVariable Long id,
            @RequestBody UserDTO.UpdateAvatarRequest request) {
        return ResponseEntity.ok(userService.updateAvatar(id, request));
    }

    // ← PHẢI ĐẶT TRƯỚC /{id}/set-password
    @PutMapping("/me/set-password")
    public ResponseEntity<UserDTO.MessageResponse> setPasswordMe(
            @RequestBody UserDTO.SetPasswordRequest request,
            jakarta.servlet.http.HttpServletRequest httpRequest) {
        String authHeader = httpRequest.getHeader("Authorization");
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        return ResponseEntity.ok(userService.setPasswordByEmail(email, request));
    }

    // ← SAU
    @PutMapping("/{id}/set-password")
    public ResponseEntity<UserDTO.MessageResponse> setPassword(
            @PathVariable Long id,
            @RequestBody UserDTO.SetPasswordRequest request) {
        return ResponseEntity.ok(userService.setPassword(id, request));
    }

    @GetMapping("/me")
    public ResponseEntity<UserDTO.UserProfileResponse> getMyProfile(
            @AuthenticationPrincipal UserDetails userDetails) {
        return ResponseEntity.ok(userService.getProfileByEmail(userDetails.getUsername()));
    }

}