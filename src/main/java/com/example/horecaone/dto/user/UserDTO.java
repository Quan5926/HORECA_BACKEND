package com.example.horecaone.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;

public class UserDTO {

    // ─────────────────────────────────────────────────────────────────
    //  REQUEST DTOs
    // ─────────────────────────────────────────────────────────────────

    /** PUT /api/v1/users/{id}  — cập nhật họ tên + SĐT */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateProfileRequest {
        private String hoTen;
        private String soDienThoai;
    }

    /** PUT /api/v1/users/{id}/change-password */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ChangePasswordRequest {
        private String matKhauCu;
        private String matKhauMoi;
    }

    /** PUT /api/v1/users/{id}/avatar */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class UpdateAvatarRequest {
        private String anhDaiDien;   // URL hoặc base64
    }

    // ─────────────────────────────────────────────────────────────────
    //  RESPONSE DTOs
    // ─────────────────────────────────────────────────────────────────

    /** Thông tin profile trả về FE */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class UserProfileResponse {
        private Long            id;
        private String          email;
        private String          hoTen;
        private String          soDienThoai;
        private String          anhDaiDien;
        private String          vaiTro;
        private Boolean         trangThai;
        private LocalDateTime   ngayTao;
        private LocalDateTime   ngaySua;
        private Boolean         isGoogleUser;  // thêm
        private Boolean         hasPassword;   // thêm
    }

    /** Response cho các thao tác không cần trả data */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class MessageResponse {
        private boolean success;
        private String  message;
    }

    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class SetPasswordRequest {
        private String otp;
        private String matKhauMoi;

    }
}