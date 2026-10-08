package com.example.horecaone.service.login;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Service
public class RecaptchaService {

    private static final String RECAPTCHA_SECRET     = "6Ld2GZosAAAAAGEfoUzsFX2EhtZrV3NsgIu7PIt5";
    private static final String RECAPTCHA_VERIFY_URL = "https://www.google.com/recaptcha/api/siteverify";

    /**
     * Thêm vào application.properties để bỏ qua captcha trên localhost:
     *   app.recaptcha.skip=true
     * Mặc định false (production luôn kiểm tra bình thường).
     */
    @Value("${app.recaptcha.skip:false}")
    private boolean skipRecaptcha;

    public boolean verifyRecaptcha(String recaptchaToken) {
        // Dev mode: bỏ qua hoàn toàn (localhost HTTP không chạy được reCAPTCHA HTTPS)
        if (skipRecaptcha) {
            System.out.println("[RecaptchaService] DEV MODE — bỏ qua xác thực captcha");
            return true;
        }

        if (recaptchaToken == null || recaptchaToken.isEmpty()) {
            return false;
        }

        RestTemplate restTemplate = new RestTemplate();

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("secret", RECAPTCHA_SECRET);
        body.add("response", recaptchaToken);

        try {
            ResponseEntity<Map> recaptchaResponse = restTemplate.postForEntity(
                    RECAPTCHA_VERIFY_URL,
                    body,
                    Map.class
            );

            Map<String, Object> responseBody = recaptchaResponse.getBody();
            if (responseBody != null) {
                Boolean success = (Boolean) responseBody.get("success");
                return Boolean.TRUE.equals(success);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }
}