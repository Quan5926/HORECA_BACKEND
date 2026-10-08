package com.example.horecaone.security.login;

import com.example.horecaone.entity.login.NguoiDung;
import com.example.horecaone.repository.login.NguoiDungRepository;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserRequest;
import org.springframework.security.oauth2.client.oidc.userinfo.OidcUserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2Error;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class CustomOAuth2UserService extends OidcUserService {

    private final NguoiDungRepository nguoiDungRepository;
    private final JwtUtil jwtUtil;

    public CustomOAuth2UserService(NguoiDungRepository nguoiDungRepository, JwtUtil jwtUtil) {
        this.nguoiDungRepository = nguoiDungRepository;
        this.jwtUtil = jwtUtil;
    }

    @Override
    public OidcUser loadUser(OidcUserRequest userRequest) throws OAuth2AuthenticationException {
        OidcUser oidcUser = super.loadUser(userRequest);
        String email = oidcUser.getEmail();

        // Chặn nếu email đã đăng ký thủ công (không phải Google)
        Optional<NguoiDung> existingUser = nguoiDungRepository.findByEmail(email);
        if (existingUser.isPresent() && !existingUser.get().isGoogleUser()) {
            throw new OAuth2AuthenticationException(
                    new OAuth2Error(
                            "email_already_exists",
                            "Email này đã đăng ký bằng mật khẩu. Vui lòng đăng nhập bằng email và mật khẩu.",
                            null
                    )
            );
        }

        String fullName = oidcUser.getFullName();
        final String finalName = (fullName == null || fullName.isBlank())
                ? email.split("@")[0]
                : fullName;

        System.out.println("Google Email: " + email);
        System.out.println("Google Name: " + finalName);

        // Tìm hoặc tạo user
        NguoiDung user = nguoiDungRepository.findByEmail(email)
                .orElseGet(() -> {
                    System.out.println(">>> Tạo user Google mới: " + email);
                    NguoiDung newUser = new NguoiDung();
                    newUser.setEmail(email);
                    newUser.setVaiTro("KHACH_HANG");
                    newUser.setTrangThai(true);
                    newUser.setMatKhauHash("GOOGLE_LOGIN_NO_PASS");
                    newUser.setGoogleUser(true); // ← đánh dấu là Google user
                    return newUser;
                });

        // Luôn cập nhật tên mới nhất từ Google
        user.setHoTen(finalName);
        user = nguoiDungRepository.save(user);
        System.out.println(">>> user.getId() sau save = " + user.getId());

        // hasPassword: true nếu user đã tạo mật khẩu riêng
        boolean hasPassword = !"GOOGLE_LOGIN_NO_PASS".equals(user.getMatKhauHash());

        // Tạo JWT
        String token = jwtUtil.generateToken(user.getEmail(), user.getVaiTro(), finalName);
        System.out.println("JWT created: " + token.substring(0, 20) + "...");

        // Inject vào attributes
        Map<String, Object> customAttributes = new HashMap<>(oidcUser.getAttributes());
        customAttributes.put("token",       token);
        customAttributes.put("email",       email);
        customAttributes.put("name",        finalName);
        customAttributes.put("role",        user.getVaiTro());
        customAttributes.put("id",          user.getId() != null ? String.valueOf(user.getId()) : "");
        customAttributes.put("hasPassword", hasPassword);

        System.out.println("hasPassword: " + hasPassword);
        System.out.println("=== CustomOAuth2UserService END ===");

        return new DefaultOidcUser(
                oidcUser.getAuthorities(),
                oidcUser.getIdToken(),
                oidcUser.getUserInfo(),
                "email"
        ) {
            @Override
            public Map<String, Object> getAttributes() {
                return customAttributes;
            }
        };
    }
}