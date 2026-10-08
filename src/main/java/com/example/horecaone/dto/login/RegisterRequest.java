package com.example.horecaone.dto.login;

public class RegisterRequest {
    private String otp;   // dùng cho bước verify
    private String email;
    private String phone;
    private String fullname;
    private String password;

    // Thêm trường này để hứng token từ Vue.js gửi lên
    private String recaptchaToken;

    // Getters & Setters
    public String getOtp() { return otp; }
    public void setOtp(String otp) { this.otp = otp; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    // Thêm Getter & Setter cho recaptchaToken
    public String getRecaptchaToken() { return recaptchaToken; }
    public void setRecaptchaToken(String recaptchaToken) { this.recaptchaToken = recaptchaToken; }
}