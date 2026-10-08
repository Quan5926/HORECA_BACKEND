package com.example.horecaone.entity.banner;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "BANNER_SLIDER")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Banner {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TIEU_DE", columnDefinition = "nvarchar(510)")
    private String tieuDe;

    @Column(name = "DUONG_DAN_ANH", nullable = false, columnDefinition = "nvarchar(1000)")
    private String duongDanAnh;

    @Column(name = "LIEN_KET", columnDefinition = "nvarchar(1000)")
    private String lienKet;

    @Column(name = "THU_TU")
    private Integer thuTu;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai;

    @Column(name = "NGUOI_TAO")
    private Long nguoiTao;

    @Column(name = "NGAY_TAO", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    @Column(name = "VI_TRI", length = 50)
    private String viTri;

    // Tự động gán ngày tạo khi insert mới vào DB
    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        if (this.trangThai == null) this.trangThai = true;
        if (this.thuTu == null) this.thuTu = 1;
    }

    // Tự động cập nhật ngày sửa
    @PreUpdate
    protected void onUpdate() {
        this.ngaySua = LocalDateTime.now();
    }
}