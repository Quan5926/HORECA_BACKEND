// service/email/EmailService.java
package com.example.horecaone.service.email;

import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    public void sendSetPasswordOtp(String toEmail, String otp) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(toEmail);
        msg.setSubject("[HORECA VN] Mã xác nhận tạo mật khẩu");
        msg.setText("""
        Xin chào,

        Bạn vừa yêu cầu tạo mật khẩu cho tài khoản đăng nhập bằng Google \
        tại hệ thống HORECA VN.

        Mã xác nhận của bạn là: %s

        Mã có hiệu lực trong 5 phút.
        Vui lòng không chia sẻ mã này với bất kỳ ai.

        Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email này.

        Trân trọng,
        Công ty Cổ phần Sản xuất và Thương mại HORECA VN
        Hotline: 0786 667 447
        Website: horecavietnam.com
        """.formatted(otp));
        mailSender.send(msg);
    }
}