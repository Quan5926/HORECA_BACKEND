package com.example.horecaone.service.user;

import com.example.horecaone.dto.user.UserDTO;

public interface UserService {

    /** Lấy thông tin profile theo ID */
    UserDTO.UserProfileResponse getProfile(Long userId);

    /** Cập nhật họ tên + số điện thoại */
    UserDTO.UserProfileResponse updateProfile(Long userId, UserDTO.UpdateProfileRequest request);

    /** Đổi mật khẩu — dùng MD5PasswordEncoder khớp với hệ thống hiện tại */
    UserDTO.MessageResponse changePassword(Long userId, UserDTO.ChangePasswordRequest request);

    /** Cập nhật ảnh đại diện (URL hoặc base64) */
    UserDTO.UserProfileResponse updateAvatar(Long userId, UserDTO.UpdateAvatarRequest request);

    /** Tạo mật khẩu lần đầu cho Google user (chưa có mật khẩu) */
    UserDTO.MessageResponse setPassword(Long userId, UserDTO.SetPasswordRequest request);

    /** Tạo mật khẩu lần đầu cho Google user — lấy user từ email (JWT) */
    UserDTO.MessageResponse setPasswordByEmail(String email, UserDTO.SetPasswordRequest request);

    UserDTO.UserProfileResponse getProfileByEmail(String email);


}