package com.example.horecaone.entity.contact;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "LIEN_HE")
public class LienHe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID", precision = 18, scale = 0)
    private Long id;

    @Column(name = "HO_TEN", nullable = false, length = 100)
    private String hoTen;

    @Column(name = "EMAIL", nullable = false, length = 255)
    private String email;

    @Column(name = "SO_DIEN_THOAI", length = 20)
    private String soDienThoai;

    @Column(name = "TIEU_DE", nullable = false, length = 255)
    private String tieuDe;

    @Column(name = "NOI_DUNG", nullable = false, columnDefinition = "NVARCHAR(MAX)")
    private String noiDung;

    @Column(name = "TRANG_THAI", nullable = false, length = 20)
    private String trangThai = "CHUA_DOC";

    @Column(name = "GHI_CHU_ADMIN", columnDefinition = "NVARCHAR(MAX)")
    private String ghiChuAdmin;

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