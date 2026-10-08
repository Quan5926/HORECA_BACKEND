package com.example.horecaone.entity.login;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;

@Entity
@Table(name = "NGUOI_DUNG")
@Data
public class NguoiDung implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL", unique = true, nullable = false)
    private String email;

    @Column(name = "SO_DIEN_THOAI")
    private String soDienThoai;

    @Column(name = "HO_TEN")
    private String hoTen;

    @Column(name = "MAT_KHAU_HASH", nullable = false)
    private String matKhauHash;

    @Column(name = "VAI_TRO", nullable = false)
    private String vaiTro;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = true;

    @Column(name = "ANH_DAI_DIEN")
    private String anhDaiDien;

    @Column(name = "is_google_user")
    private boolean googleUser = false; // ← bỏ prefix "is" để Lombok không sinh sai

    // Các trường audit
    @Column(name = "NGUOI_TAO")
    private Long nguoiTao;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao = LocalDateTime.now();

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    // ── UserDetails ──────────────────────────────────────────────
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(new SimpleGrantedAuthority("ROLE_" + vaiTro));
    }

    @Override public String getPassword()  { return matKhauHash; }
    @Override public String getUsername()  { return email; }
    @Override public boolean isAccountNonExpired()     { return true; }
    @Override public boolean isAccountNonLocked()      { return trangThai; }
    @Override public boolean isCredentialsNonExpired() { return true; }
    @Override public boolean isEnabled()               { return trangThai; }
}