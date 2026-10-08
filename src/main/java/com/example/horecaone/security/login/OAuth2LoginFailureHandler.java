package com.example.horecaone.security.login;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
public class OAuth2LoginFailureHandler implements AuthenticationFailureHandler {

    // ĐỔI
    private static final String FRONTEND = "http://horecavietnam.com";

    @Override
    public void onAuthenticationFailure(HttpServletRequest request,
                                        HttpServletResponse response,
                                        AuthenticationException exception) throws IOException {

        String message = "Đăng nhập Google thất bại. Vui lòng thử lại.";

        if (exception instanceof OAuth2AuthenticationException oauthEx) {
            String code = oauthEx.getError().getErrorCode();
            if ("email_already_exists".equals(code)) {
                message = "Email này đã đăng ký bằng mật khẩu. Vui lòng đăng nhập thường.";
            }
        }

        System.out.println("OAuth2 login thất bại: " + exception.getMessage());

        String redirectUrl = FRONTEND + "/dang-nhap?googleError="
                + URLEncoder.encode(message, StandardCharsets.UTF_8);
        response.sendRedirect(redirectUrl);
    }
}