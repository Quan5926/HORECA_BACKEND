package com.example.horecaone.entity.voucher;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "KHUYEN_MAI_FLASH")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class KhuyenMaiFlash {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "CHI_TIET_SAN_PHAM_ID", nullable = false)
    private Long chiTietSanPhamId;

    // 1–100, giảm theo %
    @Column(name = "PHAN_TRAM_GIAM_GIA", nullable = false)
    private Short phanTramGiamGia;

    @Column(name = "NGAY_BAT_DAU", nullable = false)
    private LocalDateTime ngayBatDau;

    @Column(name = "NGAY_KET_THUC", nullable = false)
    private LocalDateTime ngayKetThuc;

    // true = đang hoạt động
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

    @PrePersist
    protected void onCreate() {
        ngayTao = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        ngaySua = LocalDateTime.now();
    }
}