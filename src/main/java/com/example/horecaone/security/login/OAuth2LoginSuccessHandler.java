package com.example.horecaone.security.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Map;

@Component
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    // ĐỔI
    private static final String FRONTEND_BASE = "http://horecavietnam.com";
    private static final String ADMIN_BASE    = "http://horecavietnam.com/admin";

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        DefaultOidcUser oidcUser = (DefaultOidcUser) authentication.getPrincipal();
        Map<String, Object> attrs = oidcUser.getAttributes();

        String  token       = String.valueOf(attrs.getOrDefault("token",       ""));
        String  email       = String.valueOf(attrs.getOrDefault("email",       ""));
        String  name        = String.valueOf(attrs.getOrDefault("name",        ""));
        String  role        = String.valueOf(attrs.getOrDefault("role",        "KHACH_HANG"));
        String id = String.valueOf(attrs.getOrDefault("id", ""));
        System.out.println(">>> attrs.get('id') = '" + attrs.get("id") + "'");  // THÊM
        System.out.println(">>> id string = '" + id + "'");  // THÊM

// Thêm dòng này để debug
        System.out.println(">>> id value: '" + id + "', isEmpty: " + id.isEmpty());

        if (id.isEmpty() || "null".equals(id)) {
            // Fallback: query DB theo email
            System.out.println(">>> WARN: id rỗng cho email " + email);
        }
        boolean hasPassword = Boolean.TRUE.equals(attrs.get("hasPassword"));

        // Admin → redirect về port 5174, không cần set-password flow
        boolean isAdmin = "QUAN_TRI".equals(role) || "ROLE_QUAN_TRI".equals(role);
        if (isAdmin) {
            System.out.println(">>> Admin redirect → " + ADMIN_BASE);
            response.sendRedirect(ADMIN_BASE + "/?token=" + encode(token));
            return;
        }

        // Khách hàng → redirect về /dang-nhap kèm đủ params
        String redirectUrl = FRONTEND_BASE + "/dang-nhap"
                + "?token="        + encode(token)
                + "&isGoogleUser=true"
                + "&hasPassword="  + hasPassword
                + "&email="        + encode(email)
                + "&id="           + encode(id)
                + "&role="         + encode(role)
                + "&name="         + encode(name);

        System.out.println(">>> Google redirect → hasPassword=" + hasPassword + " | " + email);
        response.sendRedirect(redirectUrl);
    }

    private String encode(String value) {
        return URLEncoder.encode(value, StandardCharsets.UTF_8);
    }
}