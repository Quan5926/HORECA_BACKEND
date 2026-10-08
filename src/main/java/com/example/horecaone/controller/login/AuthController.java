package com.example.horecaone.controller.login;

import com.example.horecaone.dto.login.AuthResponse;
import com.example.horecaone.dto.login.ForgotPasswordRequest;
import com.example.horecaone.dto.login.LoginRequest;
import com.example.horecaone.dto.login.RegisterRequest;
import com.example.horecaone.entity.login.NguoiDung;
import com.example.horecaone.security.login.JwtUtil;
import com.example.horecaone.service.login.ForgotPasswordService;
import com.example.horecaone.service.login.RecaptchaService;
import com.example.horecaone.service.login.RegisterService;
import com.example.horecaone.repository.login.NguoiDungRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager  authenticationManager;
    private final JwtUtil                jwtUtil;
    private final RegisterService        registerService;
    private final NguoiDungRepository    nguoiDungRepository;
    private final RecaptchaService       recaptchaService;
    private final ForgotPasswordService  forgotPasswordService;

    public AuthController(AuthenticationManager authenticationManager,
                          JwtUtil jwtUtil,
                          RegisterService registerService,
                          NguoiDungRepository nguoiDungRepository,
                          RecaptchaService recaptchaService,
                          ForgotPasswordService forgotPasswordService) {
        this.authenticationManager = authenticationManager;
        this.jwtUtil               = jwtUtil;
        this.registerService       = registerService;
        this.nguoiDungRepository   = nguoiDungRepository;
        this.recaptchaService      = recaptchaService;
        this.forgotPasswordService = forgotPasswordService;
    }

    // ── ADMIN LOGIN ───────────────────────────────────────────────────────────
    @PostMapping("/admin-login")
    public ResponseEntity<?> adminLogin(@RequestBody LoginRequest request) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(), request.getPassword()));

            NguoiDung user = nguoiDungRepository.findByEmail(authentication.getName())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin người dùng"));

            if (!"QUAN_TRI".equals(user.getVaiTro())) {
                return ResponseEntity.status(403).body("Chỉ tài khoản QUAN_TRI mới được đăng nhập!");
            }

            String token = jwtUtil.generateToken(user.getEmail(), user.getVaiTro());
            return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), user.getVaiTro(), user.getId()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401).body("Email/Số điện thoại hoặc mật khẩu không đúng!");
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi hệ thống: " + e.getMessage());
        }
    }

    // ── USER LOGIN ────────────────────────────────────────────────────────────
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // 1. Kiểm tra CAPTCHA
            boolean isCaptchaValid = recaptchaService.verifyRecaptcha(request.getRecaptchaToken());
            if (!isCaptchaValid) {
                return ResponseEntity.status(400)
                        .body(Map.of("message", "Xác thực Captcha không hợp lệ hoặc đã hết hạn!"));
            }

            // 2. Xác thực tài khoản / mật khẩu
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(
                            request.getIdentifier(), request.getPassword()));

            NguoiDung user = nguoiDungRepository.findByEmail(authentication.getName())
                    .or(() -> nguoiDungRepository.findBySoDienThoai(authentication.getName()))
                    .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại trên hệ thống"));

            String role  = user.getVaiTro();
            String token = jwtUtil.generateToken(user.getEmail(), role);
            return ResponseEntity.ok(new AuthResponse(token, user.getEmail(), role, user.getId()));

        } catch (BadCredentialsException e) {
            return ResponseEntity.status(401)
                    .body(Map.of("message", "Email/Số điện thoại hoặc mật khẩu không đúng!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("message", "Lỗi hệ thống: " + e.getMessage()));
        }
    }

    // ── REGISTER ──────────────────────────────────────────────────────────────
    @PostMapping("/register/request-otp")
    public ResponseEntity<?> requestOtp(@RequestBody RegisterRequest request) {
        try {
            // 1. Kiểm tra CAPTCHA trước khi gửi OTP
            boolean isCaptchaValid = recaptchaService.verifyRecaptcha(request.getRecaptchaToken());
            if (!isCaptchaValid) {
                return ResponseEntity.status(400)
                        .body(Map.of("message", "Xác thực Captcha không hợp lệ hoặc đã hết hạn!"));
            }

            // 2. Nếu Captcha OK thì mới gọi service gửi OTP
            registerService.requestOtp(request);
            return ResponseEntity.ok(Map.of("message", "Mã OTP đã được gửi đến email của bạn!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/register/verify")
    public ResponseEntity<?> verifyOtp(@RequestBody RegisterRequest request) {
        try {
            String userId = registerService.verifyAndRegister(request);
            return ResponseEntity.ok(Map.of(
                    "message", "Đăng ký thành công!",
                    "userId",  userId,
                    "email",   request.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    // ── FORGOT PASSWORD ───────────────────────────────────────────────────────

    /**
     * BƯỚC 1 — Gửi OTP về email
     * POST /api/auth/forgot-password/request-otp
     * Body: { email, recaptchaToken }
     */
    @PostMapping("/forgot-password/request-otp")
    public ResponseEntity<?> forgotRequestOtp(@RequestBody ForgotPasswordRequest request) {
        try {
            // Kiểm tra captcha
            boolean captchaOk = recaptchaService.verifyRecaptcha(request.getRecaptchaToken());
            if (!captchaOk) {
                return ResponseEntity.badRequest()
                        .body(Map.of("message", "Xác thực Captcha không hợp lệ!"));
            }

            forgotPasswordService.sendOtp(request.getEmail());
            return ResponseEntity.ok(Map.of("message", "Mã OTP đã được gửi đến email của bạn!"));

        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * BƯỚC 2 — Xác thực OTP, nhận về resetToken
     * POST /api/auth/forgot-password/verify-otp
     * Body: { email, otp }
     */
    @PostMapping("/forgot-password/verify-otp")
    public ResponseEntity<?> forgotVerifyOtp(@RequestBody ForgotPasswordRequest request) {
        try {
            String resetToken = forgotPasswordService.verifyOtp(
                    request.getEmail(), request.getOtp());

            return ResponseEntity.ok(Map.of(
                    "message",    "Xác thực OTP thành công!",
                    "resetToken", resetToken
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    /**
     * BƯỚC 3 — Đặt lại mật khẩu mới
     * POST /api/auth/forgot-password/reset
     * Body: { email, otp, newPassword, resetToken }
     */
    @PostMapping("/forgot-password/reset")
    public ResponseEntity<?> forgotReset(@RequestBody ForgotPasswordRequest request) {
        try {
            forgotPasswordService.resetPassword(
                    request.getEmail(),
                    request.getResetToken(),
                    request.getNewPassword()
            );
            return ResponseEntity.ok(Map.of("message", "Mật khẩu đã được cập nhật thành công!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/admin/register/request-otp")
    public ResponseEntity<?> adminRequestOtp(@RequestBody RegisterRequest request) {
        try {
            boolean isCaptchaValid = recaptchaService.verifyRecaptcha(request.getRecaptchaToken());
            if (!isCaptchaValid) {
                return ResponseEntity.status(400)
                        .body(Map.of("message", "Xác thực Captcha không hợp lệ hoặc đã hết hạn!"));
            }
            registerService.requestOtp(request); // dùng chung validate + gửi OTP
            return ResponseEntity.ok(Map.of("message", "Mã OTP đã được gửi đến email của bạn!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }

    @PostMapping("/admin/register/verify")
    public ResponseEntity<?> adminVerifyOtp(@RequestBody RegisterRequest request) {
        try {
            String userId = registerService.verifyAndRegisterAdmin(request); // ← method mới
            return ResponseEntity.ok(Map.of(
                    "message", "Tài khoản Admin đã được tạo thành công!",
                    "userId",  userId,
                    "email",   request.getEmail()
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("message", e.getMessage()));
        }
    }
}