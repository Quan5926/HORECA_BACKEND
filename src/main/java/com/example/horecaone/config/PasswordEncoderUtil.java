package com.example.horecaone.config;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

public class PasswordEncoderUtil {

    /**
     * Mã hóa MD5 - trả về đúng 32 ký tự hex (không cần thư viện ngoài)
     */
    public static String md5(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] hashBytes = md.digest(password.getBytes(StandardCharsets.UTF_8));

            StringBuilder hexString = new StringBuilder();
            for (byte b : hashBytes) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Lỗi hash MD5", e);
        }
    }
}