package com.example.horecaone.entity.user;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "NGUOI_DUNG")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "EMAIL", nullable = false, length = 510)
    private String email;

    @Column(name = "SO_DIEN_THOAI", length = 40)
    private String soDienThoai;

    @Column(name = "HO_TEN", length = 200)
    private String hoTen;

    @Column(name = "MAT_KHAU_HASH", length = 510)
    private String matKhauHash;

    @Column(name = "VAI_TRO", nullable = false, length = 40)
    private String vaiTro;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai = true;

    @Column(name = "NGUOI_TAO")
    private Long nguoiTao;

    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    @Column(name = "ANH_DAI_DIEN", columnDefinition = "NVARCHAR(MAX)")
    private String anhDaiDien;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
        if (trangThai == null) trangThai = true;
    }

    @PreUpdate
    protected void onUpdate() {
        ngaySua = LocalDateTime.now();
    }
}