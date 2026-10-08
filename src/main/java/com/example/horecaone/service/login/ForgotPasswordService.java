package com.example.horecaone.service.login;

import com.example.horecaone.entity.login.NguoiDung;
import com.example.horecaone.repository.login.NguoiDungRepository;
import com.example.horecaone.security.login.MD5PasswordEncoder;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ForgotPasswordService — không dùng Redis.
 * Lưu OTP & resetToken trong ConcurrentHashMap với thời gian hết hạn.
 *
 * Luồng 3 bước:
 *  1. sendOtp(email)                        → sinh OTP 6 số, lưu 5 phút, gửi mail
 *  2. verifyOtp(email, otp)                 → xác thực OTP → trả resetToken (10 phút)
 *  3. resetPassword(email, token, newPass)  → xác thực token → cập nhật mật khẩu
 */
@Service
@RequiredArgsConstructor
public class ForgotPasswordService {

    private final NguoiDungRepository nguoiDungRepository;
    private final JavaMailSender       mailSender;
    private final MD5PasswordEncoder   passwordEncoder;

    // ── TTL (milliseconds) ────────────────────────────────────────────────────
    private static final long OTP_TTL_MS   = 5  * 60 * 1_000L;  // 5 phút
    private static final long TOKEN_TTL_MS = 10 * 60 * 1_000L;  // 10 phút

    // ── In-memory stores ──────────────────────────────────────────────────────
    private final Map<String, OtpEntry> otpStore   = new ConcurrentHashMap<>();
    private final Map<String, OtpEntry> tokenStore = new ConcurrentHashMap<>();

    // ─────────────────────────────────────────────────────────────────────────
    // BƯỚC 1 — Gửi OTP
    // ─────────────────────────────────────────────────────────────────────────
    public void sendOtp(String email) {
        String key = email.trim().toLowerCase();

        NguoiDung user = nguoiDungRepository.findByEmail(key)
                .orElseThrow(() -> new RuntimeException("Email không tồn tại trên hệ thống!"));

        // ← THÊM: chặn Google user chưa có mật khẩu
        if ("GOOGLE_LOGIN_NO_PASS".equals(user.getMatKhauHash())) {
            throw new RuntimeException(
                    "Tài khoản này đăng nhập bằng Google và chưa có mật khẩu. " +
                            "Vui lòng đăng nhập bằng Google và tạo mật khẩu trong phần Tài khoản.");
        }

        String otp      = generateOtp();
        long   expireAt = Instant.now().toEpochMilli() + OTP_TTL_MS;

        otpStore.put(key, new OtpEntry(otp, expireAt));
        sendOtpMail(key, user.getHoTen(), otp);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BƯỚC 2 — Xác thực OTP → trả resetToken
    // ─────────────────────────────────────────────────────────────────────────
    public String verifyOtp(String email, String otp) {
        String   key   = email.trim().toLowerCase();
        OtpEntry entry = otpStore.get(key);

        if (entry == null || entry.isExpired()) {
            otpStore.remove(key);
            throw new RuntimeException("Mã OTP đã hết hạn. Vui lòng yêu cầu mã mới!");
        }
        if (!entry.value.equals(otp.trim())) {
            throw new RuntimeException("Mã OTP không đúng!");
        }

        otpStore.remove(key);

        String resetToken = UUID.randomUUID().toString();
        tokenStore.put(key, new OtpEntry(resetToken, Instant.now().toEpochMilli() + TOKEN_TTL_MS));

        return resetToken;
    }

    // ─────────────────────────────────────────────────────────────────────────
    // BƯỚC 3 — Đặt lại mật khẩu
    // ─────────────────────────────────────────────────────────────────────────
    public void resetPassword(String email, String resetToken, String newPassword) {
        String   key   = email.trim().toLowerCase();
        OtpEntry entry = tokenStore.get(key);

        if (entry == null || entry.isExpired()) {
            tokenStore.remove(key);
            throw new RuntimeException("Phiên đặt lại mật khẩu đã hết hạn. Vui lòng thực hiện lại từ đầu!");
        }
        if (!entry.value.equals(resetToken)) {
            throw new RuntimeException("Token không hợp lệ!");
        }

        NguoiDung user = nguoiDungRepository.findByEmail(key)
                .orElseThrow(() -> new RuntimeException("Tài khoản không tồn tại!"));

        user.setMatKhauHash(passwordEncoder.encode(newPassword));
        nguoiDungRepository.save(user);

        tokenStore.remove(key);
    }

    // ─────────────────────────────────────────────────────────────────────────
    // HELPERS
    // ─────────────────────────────────────────────────────────────────────────

    private String generateOtp() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }

    private void sendOtpMail(String to, String name, String otp) {
        try {
            jakarta.mail.internet.MimeMessage msg = mailSender.createMimeMessage();
            org.springframework.mail.javamail.MimeMessageHelper helper =
                    new org.springframework.mail.javamail.MimeMessageHelper(msg, false, "UTF-8");
            helper.setTo(to);
            helper.setSubject("[HORECA VN] Mã xác thực đặt lại mật khẩu");
            helper.setText(
                    "Xin chào " + (name != null ? name : "Quý khách") + ",\n\n" +
                            "Bạn vừa yêu cầu đặt lại mật khẩu tài khoản tại hệ thống HORECA VN.\n\n" +
                            "Mã xác nhận của bạn là:  " + otp + "\n\n" +
                            "Mã có hiệu lực trong 5 phút.\n" +
                            "Vui lòng không chia sẻ mã này với bất kỳ ai.\n" +
                            "Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.\n\n" +
                            "Trân trọng,\n" +
                            "Công ty Cổ phần Sản xuất và Thương mại HORECA VN\n" +
                            "Hotline: 0786 667 447\n" +
                            "Website: horecavietnam.com"
            );
            mailSender.send(msg);
        } catch (Exception e) {
            throw new RuntimeException("Không thể gửi email: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // INNER CLASS (dùng class thay record để tương thích Java 11+)
    // ─────────────────────────────────────────────────────────────────────────
    private static class OtpEntry {
        final String value;
        final long   expireAt;

        OtpEntry(String value, long expireAt) {
            this.value    = value;
            this.expireAt = expireAt;
        }

        boolean isExpired() {
            return Instant.now().toEpochMilli() > expireAt;
        }
    }
}