// controller/auth/OtpController.java
package com.example.horecaone.controller.auth;

import com.example.horecaone.exception.BadRequestException;
import com.example.horecaone.repository.user.UserRepository;
import com.example.horecaone.security.login.JwtUtil;
import com.example.horecaone.service.email.EmailService;
import com.example.horecaone.service.otp.OtpService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService     otpService;
    private final EmailService   emailService;
    private final JwtUtil        jwtUtil;
    private final UserRepository userRepository;

    @PostMapping("/send-set-password-otp")
    public ResponseEntity<?> sendSetPasswordOtp(HttpServletRequest request) {
        String email = extractEmail(request);

        // Chỉ cho phép nếu chưa có mật khẩu
        userRepository.findByEmail(email).ifPresent(user -> {
            if (!"GOOGLE_LOGIN_NO_PASS".equals(user.getMatKhauHash())) {
                throw new BadRequestException(
                        "Tài khoản đã có mật khẩu. Vui lòng dùng chức năng đổi mật khẩu.");
            }
        });

        String otp = otpService.generateAndStore(email);
        emailService.sendSetPasswordOtp(email, otp);

        return ResponseEntity.ok(Map.of(
                "success", true,
                "message", "Mã OTP đã được gửi đến " + email
        ));
    }

    private String extractEmail(HttpServletRequest request) {
        String auth = request.getHeader("Authorization");
        if (auth == null || !auth.startsWith("Bearer "))
            throw new BadRequestException("Thiếu token xác thực");
        return jwtUtil.extractEmail(auth.substring(7));
    }
}