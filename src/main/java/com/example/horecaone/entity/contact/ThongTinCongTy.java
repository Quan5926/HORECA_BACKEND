package com.example.horecaone.entity.contact;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "THONG_TIN_CONG_TY")
public class ThongTinCongTy {

    @Id
    @Column(name = "ID", precision = 18, scale = 0)
    private Long id = 1L;

    @Column(name = "TEN_CONG_TY", nullable = false, length = 510)
    private String tenCongTy;

    @Column(name = "SO_DIEN_THOAI", nullable = false, length = 40)
    private String soDienThoai;

    @Column(name = "EMAIL", nullable = false, length = 510)
    private String email;

    @Column(name = "VAN_PHONG", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String vanPhong;

    @Column(name = "NHA_MAY", columnDefinition = "NVARCHAR(MAX)")
    private String nhaMay;

    @Column(name = "MAP_URL", columnDefinition = "NVARCHAR(MAX)")
    private String mapUrl;

    @Column(name = "NGUOI_TAO", precision = 18, scale = 0)
    private Long nguoiTao;

    @Column(name = "NGAY_TAO", nullable = false, updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_SUA", precision = 18, scale = 0)
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngaySua = LocalDateTime.now();
    }
}