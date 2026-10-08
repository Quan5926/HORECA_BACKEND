package com.example.horecaone.service.user;

import com.example.horecaone.dto.user.UserDTO;
import com.example.horecaone.entity.user.User;
import com.example.horecaone.exception.BadRequestException;
import com.example.horecaone.exception.ResourceNotFoundException;
import com.example.horecaone.repository.user.UserRepository;
import com.example.horecaone.security.login.MD5PasswordEncoder;
import com.example.horecaone.service.otp.OtpService;
import com.example.horecaone.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository     userRepository;
    private final MD5PasswordEncoder passwordEncoder;
    private final OtpService otpService;

    private static final Pattern PWD_PATTERN =
            Pattern.compile("^(?=.*[A-Z])(?=.*[0-9])(?=.*[^A-Za-z0-9]).{8,}$");

    private static final Pattern PHONE_PATTERN =
            Pattern.compile("^(0|\\+84)[0-9]{9,10}$");

    // ──────────────────────────────────────────────────────────────
    //  GET PROFILE
    // ──────────────────────────────────────────────────────────────
    @Override
    @Transactional(readOnly = true)
    public UserDTO.UserProfileResponse getProfile(Long userId) {
        return toResponse(findById(userId));
    }

    // ──────────────────────────────────────────────────────────────
    //  UPDATE PROFILE
    // ──────────────────────────────────────────────────────────────

    @Override
    @Transactional
    public UserDTO.UserProfileResponse updateProfile(Long userId, UserDTO.UpdateProfileRequest req) {
        if (!StringUtils.hasText(req.getHoTen()))
            throw new BadRequestException("Họ tên không được để trống");
        if (req.getHoTen().trim().length() > 200)
            throw new BadRequestException("Họ tên không được vượt quá 200 ký tự");

        String phone = null;
        if (StringUtils.hasText(req.getSoDienThoai())) {
            phone = req.getSoDienThoai().trim().replaceAll("\\s+", "");
            if (!PHONE_PATTERN.matcher(phone).matches())
                throw new BadRequestException("Số điện thoại không đúng định dạng Việt Nam");
            // Bỏ check trùng SĐT
        }

        try {
            int updated = userRepository.updateProfile(userId, req.getHoTen().trim(), phone, LocalDateTime.now());
            if (updated == 0)
                throw new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId);
        } catch (DataIntegrityViolationException e) {
            throw new BadRequestException("Lỗi cập nhật dữ liệu");
        }

        return toResponse(findById(userId));
    }

    // ──────────────────────────────────────────────────────────────
    //  CHANGE PASSWORD
    // ──────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserDTO.MessageResponse changePassword(Long userId, UserDTO.ChangePasswordRequest req) {
        User user = findById(userId);

        if (!StringUtils.hasText(req.getMatKhauCu())) {
            throw new BadRequestException("Vui lòng nhập mật khẩu hiện tại");
        }
        if (!passwordEncoder.matches(req.getMatKhauCu(), user.getMatKhauHash())) {
            throw new BadRequestException("Mật khẩu hiện tại không đúng");
        }
        if (!StringUtils.hasText(req.getMatKhauMoi())) {
            throw new BadRequestException("Mật khẩu mới không được để trống");
        }
        if (!PWD_PATTERN.matcher(req.getMatKhauMoi()).matches()) {
            throw new BadRequestException(
                    "Mật khẩu mới phải có ít nhất 8 ký tự, 1 chữ hoa, 1 chữ số và 1 ký tự đặc biệt");
        }
        if (req.getMatKhauCu().equals(req.getMatKhauMoi())) {
            throw new BadRequestException("Mật khẩu mới không được trùng mật khẩu cũ");
        }

        userRepository.updatePassword(
                userId,
                passwordEncoder.encode(req.getMatKhauMoi()),
                LocalDateTime.now()
        );

        return UserDTO.MessageResponse.builder()
                .success(true)
                .message("Đổi mật khẩu thành công")
                .build();
    }

    // ──────────────────────────────────────────────────────────────
    //  UPDATE AVATAR
    // ──────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserDTO.UserProfileResponse updateAvatar(Long userId, UserDTO.UpdateAvatarRequest req) {
        if (!StringUtils.hasText(req.getAnhDaiDien()))
            throw new BadRequestException("Ảnh đại diện không hợp lệ");
        // Base64 của 2MB ≈ 2,800,000 ký tự
        if (req.getAnhDaiDien().length() > 2_800_000)
            throw new BadRequestException("Ảnh đại diện vượt quá kích thước cho phép (2MB)");

        int updated = userRepository.updateAvatar(userId, req.getAnhDaiDien(), LocalDateTime.now());
        if (updated == 0)
            throw new ResourceNotFoundException("Không tìm thấy người dùng với ID: " + userId);

        return toResponse(findById(userId));
    }

    // ──────────────────────────────────────────────────────────────
    //  HELPERS
    // ──────────────────────────────────────────────────────────────
    private User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng với ID: " + id));
    }

    private UserDTO.UserProfileResponse toResponse(User u) {
        boolean isGoogle = "GOOGLE_LOGIN_NO_PASS".equals(u.getMatKhauHash());
        boolean hasPwd   = u.getMatKhauHash() != null && !isGoogle;

        return UserDTO.UserProfileResponse.builder()
                .id(u.getId())
                .email(u.getEmail())
                .hoTen(u.getHoTen())
                .soDienThoai(u.getSoDienThoai())
                .anhDaiDien(u.getAnhDaiDien())
                .vaiTro(u.getVaiTro())
                .trangThai(u.getTrangThai())
                .ngayTao(u.getNgayTao())
                .ngaySua(u.getNgaySua())
                .isGoogleUser(isGoogle) // ← THÊM
                .hasPassword(hasPwd)    // ← THÊM
                .build();
    }

    // ──────────────────────────────────────────────────────────────
//  SET PASSWORD (Google user — lần đầu tạo mật khẩu)
// ──────────────────────────────────────────────────────────────
    @Override
    @Transactional
    public UserDTO.MessageResponse setPassword(Long userId, UserDTO.SetPasswordRequest req) {
        User user = findById(userId);

        if (!"GOOGLE_LOGIN_NO_PASS".equals(user.getMatKhauHash())) {
            throw new BadRequestException("Tài khoản đã có mật khẩu. Vui lòng dùng chức năng đổi mật khẩu.");
        }
        if (!StringUtils.hasText(req.getMatKhauMoi())) {
            throw new BadRequestException("Mật khẩu không được để trống");
        }
        if (!PWD_PATTERN.matcher(req.getMatKhauMoi()).matches()) {
            throw new BadRequestException(
                    "Mật khẩu phải có ít nhất 8 ký tự, 1 chữ hoa, 1 chữ số và 1 ký tự đặc biệt");
        }

        userRepository.updatePassword(
                userId,
                passwordEncoder.encode(req.getMatKhauMoi()),
                LocalDateTime.now()
        );

        return UserDTO.MessageResponse.builder()
                .success(true)
                .message("Mật khẩu đã được tạo thành công")
                .build();
    }

    @Override
    @Transactional
    public UserDTO.MessageResponse setPasswordByEmail(String email, UserDTO.SetPasswordRequest req) {

        // LOG 1: email nhận được từ JWT
        System.out.println(">>> setPasswordByEmail — email: " + email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy người dùng: " + email));

        // LOG 2: matKhauHash hiện tại trong DB
        System.out.println(">>> matKhauHash hiện tại: " + user.getMatKhauHash());
        System.out.println(">>> userId: " + user.getId());

        if (!"GOOGLE_LOGIN_NO_PASS".equals(user.getMatKhauHash())) {
            throw new BadRequestException("Tài khoản đã có mật khẩu. Vui lòng dùng chức năng đổi mật khẩu.");
        }
        if (!StringUtils.hasText(req.getMatKhauMoi())) {
            throw new BadRequestException("Mật khẩu không được để trống");
        }
        if (!PWD_PATTERN.matcher(req.getMatKhauMoi()).matches()) {
            throw new BadRequestException("Mật khẩu phải có ít nhất 8 ký tự, 1 chữ hoa, 1 chữ số và 1 ký tự đặc biệt");
        }

        // LOG 3: hash mới sẽ lưu
        String newHash = passwordEncoder.encode(req.getMatKhauMoi());
        System.out.println(">>> newHash: " + newHash);

        int rows = userRepository.updatePassword(user.getId(), newHash, LocalDateTime.now());

        // LOG 4: số row affected
        System.out.println(">>> rows updated: " + rows);

        return UserDTO.MessageResponse.builder()
                .success(true)
                .message("Mật khẩu đã được tạo thành công")
                .build();
    }

    @Override
    @Transactional(readOnly = true)
    public UserDTO.UserProfileResponse getProfileByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Không tìm thấy người dùng: " + email));
        return toResponse(user);
    }
}