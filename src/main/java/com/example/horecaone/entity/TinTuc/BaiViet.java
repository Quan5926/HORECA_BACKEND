package com.example.horecaone.entity.TinTuc;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "BAI_VIET")
public class BaiViet {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "TIEU_DE", nullable = false)
    private String tieuDe;

    @Column(name = "MO_TA_NGAN")
    private String moTaNgan;

    @Column(name = "NOI_DUNG", columnDefinition = "NVARCHAR(MAX)", nullable = false)
    private String noiDung;

    @Lob
    // Đã thêm NVARCHAR(MAX) để chứa được chuỗi ảnh Base64 siêu dài
    @Column(name = "ANH_DAI_DIEN", columnDefinition = "NVARCHAR(MAX)")
    private String anhDaiDien;

    @Column(name = "DANH_MUC")
    private String danhMuc;

    @Column(name = "LUOT_XEM")
    private Integer luotXem = 0;

    @Column(name = "TRANG_THAI")
    private Boolean trangThai = true; // 1: Active, 0: Inactive

    @Column(name = "NGUOI_TAO")
    private Long nguoiTao;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    @Column(name = "NOI_BAT")
    private Boolean noiBat = false; // 1: Nổi bật, 0: Bình thường

    @Column(name = "TAGS")
    private String tags;

    @PrePersist
    protected void onCreate() {
        this.ngayTao = LocalDateTime.now();
        if (this.luotXem == null) this.luotXem = 0;
        if (this.trangThai == null) this.trangThai = true;
        if (this.noiBat == null) this.noiBat = false;
    }


}