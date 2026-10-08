package com.example.horecaone.service.login;

import com.example.horecaone.config.PasswordEncoderUtil;
import com.example.horecaone.dto.login.RegisterRequest;
import jakarta.mail.internet.MimeMessage;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.Random;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Pattern;

@Service
public class RegisterService {

    private final JdbcTemplate jdbcTemplate;
    private final JavaMailSender mailSender;
    private final ConcurrentHashMap<String, PendingRegistration> pending = new ConcurrentHashMap<>();

    // Các mẫu Regex để validate
    private static final String EMAIL_REGEX = "^[A-Za-z0-9+_.-]+@(.+)$";
    private static final String PHONE_REGEX = "^(0|84)(3|5|7|8|9)([0-9]{8})$";
    // Mật khẩu: Ít nhất 8 ký tự, có ít nhất 1 chữ cái, 1 chữ số và 1 ký tự đặc biệt
    private static final String PWD_REGEX = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[@$!%*#?&])[A-Za-z\\d@$!%*#?&]{8,}$";
    // Họ tên: Chỉ chữ cái và khoảng trắng, từ 2 đến 50 ký tự
    private static final String NAME_REGEX = "^[\\p{L} ]{2,50}$";

    private record PendingRegistration(
            String fullname,
            String phone,
            String password,
            String otp,
            long expiryTime
    ) {}

    public RegisterService(JdbcTemplate jdbcTemplate, JavaMailSender mailSender) {
        this.jdbcTemplate = jdbcTemplate;
        this.mailSender = mailSender;
    }

    // ================== HÀM VALIDATE CHI TIẾT ==================
    private void validateRequest(RegisterRequest req) {
        // 1. Validate Họ tên
        if (req.getFullname() == null || !Pattern.matches(NAME_REGEX, req.getFullname().trim())) {
            throw new IllegalArgumentException("Họ tên không hợp lệ (chỉ chứa chữ, từ 2-50 ký tự)!");
        }

        // 2. Validate Email
        if (req.getEmail() == null || !Pattern.matches(EMAIL_REGEX, req.getEmail())) {
            throw new IllegalArgumentException("Định dạng email không hợp lệ!");
        }

        // 3. Validate SĐT — chỉ check format nếu có nhập, không bắt buộc
        if (req.getPhone() != null && !req.getPhone().isBlank()
                && !Pattern.matches(PHONE_REGEX, req.getPhone())) {
            throw new IllegalArgumentException("Số điện thoại không đúng định dạng Việt Nam!");
        }

        // 4. Validate Mật khẩu
        if (req.getPassword() == null || !Pattern.matches(PWD_REGEX, req.getPassword())) {
            throw new IllegalArgumentException("Mật khẩu tối thiểu 8 ký tự, bao gồm chữ, số và ký tự đặc biệt!");
        }

        // 5. Kiểm tra Email tồn tại (Unique)
        Integer emailCount = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM NGUOI_DUNG WHERE EMAIL = ?", Integer.class, req.getEmail());
        if (emailCount != null && emailCount > 0) {
            throw new IllegalArgumentException("Email này đã được đăng ký!");
        }

        // 6. Bỏ check trùng SĐT
    }
    // ================== BƯỚC 1: GỬI OTP ==================
    public void requestOtp(RegisterRequest req) {
        // Gọi hàm validate trước khi làm bất cứ việc gì khác
        validateRequest(req);

        String otp = String.format("%06d", new Random().nextInt(999999));
        long expiry = System.currentTimeMillis() + 300_000; // 5 phút

        pending.put(req.getEmail(), new PendingRegistration(
                req.getFullname().trim(),
                req.getPhone(),
                req.getPassword(),
                otp,
                expiry
        ));

        sendOtpEmail(req.getEmail(), otp);
    }

    // ================== BƯỚC 2: XÁC THỰC OTP + TẠO TÀI KHOẢN ==================
    public String verifyAndRegister(RegisterRequest req) {
        PendingRegistration pr = pending.get(req.getEmail());
        if (pr == null || pr.expiryTime() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("OTP đã hết hạn hoặc không tồn tại!");
        }
        if (!pr.otp().equals(req.getOtp())) {
            throw new IllegalArgumentException("Mã OTP không đúng!");
        }

        String matKhauHash = PasswordEncoderUtil.md5(pr.password());

        // Đã sửa thành QUAN_TRI theo yêu cầu của bạn
        String sql = """
    INSERT INTO NGUOI_DUNG 
    (EMAIL, SO_DIEN_THOAI, HO_TEN, MAT_KHAU_HASH, VAI_TRO, TRANG_THAI, is_google_user)
    VALUES (?, ?, ?, ?, N'KHACH_HANG', 1, 0)
    """;

        jdbcTemplate.update(sql,
                req.getEmail(),
                pr.phone(), // Dùng phone từ bản lưu tạm cho chính xác
                pr.fullname(),
                matKhauHash
        );

        String userId = jdbcTemplate.queryForObject(
                "SELECT ID FROM NGUOI_DUNG WHERE EMAIL = ?", String.class, req.getEmail());

        pending.remove(req.getEmail());
        return userId;
    }

    private void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
            helper.setTo(toEmail);
            helper.setSubject("[HORECA VN] Mã xác thực đăng ký tài khoản");

            String htmlContent = """
            <div style="font-family: Arial, sans-serif; max-width: 600px; margin: auto; border: 1px solid #eee; border-radius: 8px; overflow: hidden;">
                <div style="background: #b31f24; padding: 24px; text-align: center;">
                    <h2 style="color: #fff; margin: 0;">HORECA VN</h2>
                    <p style="color: rgba(255,255,255,0.8); margin: 4px 0 0; font-size: 13px;">
                        Công ty Cổ phần Sản xuất và Thương mại HORECA VN
                    </p>
                </div>
                <div style="padding: 32px 24px;">
                    <p style="color: #333; font-size: 15px;">Xin chào,</p>
                    <p style="color: #333; font-size: 15px;">
                        Cảm ơn bạn đã đăng ký tài khoản tại <strong>HORECA VN</strong>.
                        Mã xác thực (OTP) của bạn là:
                    </p>
                    <div style="background: #f9f9f9; border: 2px dashed #b31f24; border-radius: 8px; padding: 20px; text-align: center; margin: 24px 0;">
                        <span style="font-size: 36px; font-weight: bold; color: #b31f24; letter-spacing: 8px;">%s</span>
                    </div>
                    <p style="color: #666; font-size: 13px;">
                        ⏱ Mã có hiệu lực trong <strong>5 phút</strong>.<br>
                        🔒 Vui lòng không chia sẻ mã này với bất kỳ ai.
                    </p>
                    <p style="color: #999; font-size: 12px; margin-top: 24px;">
                        Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.
                    </p>
                </div>
                <div style="background: #f5f5f5; padding: 16px 24px; text-align: center; border-top: 1px solid #eee;">
                    <p style="color: #999; font-size: 12px; margin: 0;">
                        © 2025 Công ty Cổ phần Sản xuất và Thương mại HORECA VN<br>
                        Hotline: 0786 667 447 &nbsp;|&nbsp; Website: horecavietnam.com
                    </p>
                </div>
            </div>
            """.formatted(otp);

            helper.setText(htmlContent, true);
            mailSender.send(message);
            System.out.println("✅ GỬI EMAIL OTP THÀNH CÔNG đến: " + toEmail);
        } catch (Exception e) {
            System.err.println("❌ LỖI GỬI MAIL: " + e.getMessage());
            throw new RuntimeException("Không thể gửi email xác thực. Vui lòng thử lại sau!");
        }
    }

    public String verifyAndRegisterAdmin(RegisterRequest req) {
        PendingRegistration pr = pending.get(req.getEmail());
        if (pr == null || pr.expiryTime() < System.currentTimeMillis()) {
            throw new IllegalArgumentException("OTP đã hết hạn hoặc không tồn tại!");
        }
        if (!pr.otp().equals(req.getOtp())) {
            throw new IllegalArgumentException("Mã OTP không đúng!");
        }

        String matKhauHash = PasswordEncoderUtil.md5(pr.password());

        // ← QUAN_TRI thay vì KHACH_HANG
        String sql = """
        INSERT INTO NGUOI_DUNG 
        (EMAIL, SO_DIEN_THOAI, HO_TEN, MAT_KHAU_HASH, VAI_TRO, TRANG_THAI, is_google_user)
        VALUES (?, ?, ?, ?, N'QUAN_TRI', 1, 0)
        """;

        jdbcTemplate.update(sql,
                req.getEmail(),
                pr.phone(),
                pr.fullname(),
                matKhauHash
        );

        String userId = jdbcTemplate.queryForObject(
                "SELECT ID FROM NGUOI_DUNG WHERE EMAIL = ?", String.class, req.getEmail());

        pending.remove(req.getEmail());
        return userId;
    }
}