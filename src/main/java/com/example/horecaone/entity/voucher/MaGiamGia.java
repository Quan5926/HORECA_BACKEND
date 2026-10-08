package com.example.horecaone.entity.voucher;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "MA_GIAM_GIA")
@Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
public class MaGiamGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "MA_VOUCHER", nullable = false, length = 100)
    private String maVoucher;

    // PHAN_TRAM | CO_DINH | FREESHIP
    @Column(name = "LOAI_GIAM", nullable = false, length = 60)
    private String loaiGiam;

    @Column(name = "GIA_TRI_GIAM", nullable = false, precision = 15, scale = 2)
    private BigDecimal giaTriGiam;

    @Column(name = "DON_TOI_THIEU", nullable = false, precision = 15, scale = 2)
    private BigDecimal donToiThieu;

    // null = không giới hạn (chỉ dùng khi LOAI_GIAM = PHAN_TRAM)
    @Column(name = "GIAM_TOI_DA", precision = 15, scale = 2)
    private BigDecimal giamToiDa;

    // null = không giới hạn số lượt
    @Column(name = "GIOI_HAN_SU_DUNG")
    private Integer gioiHanSuDung;

    @Column(name = "DA_SU_DUNG", nullable = false)
    private Integer daSuDung = 0;

    @Column(name = "NGAY_BAT_DAU")
    private LocalDateTime ngayBatDau;

    @Column(name = "NGAY_KET_THUC")
    private LocalDateTime ngayKetThuc;

    @Column(name = "TRANG_THAI_HOAT_DONG", nullable = false)
    private Boolean trangThaiHoatDong = true;

    // ── Cột mới ─────────────────────────────────────────────

    // Tên / mô tả chương trình voucher (VD: "Voucher Tri Ân Tháng 3")
    @Column(name = "MO_TA", length = 255)
    private String moTa;

    // Phạm vi sản phẩm: ALL | CATEGORY | SPECIFIC
    @Column(name = "PHAM_VI_AP_DUNG", nullable = false, length = 50)
    private String phamViApDung = "ALL";

    // Đối tượng khách hàng: ALL | NEW | VIP
    @Column(name = "DOI_TUONG", nullable = false, length = 50)
    private String doiTuong = "ALL";

    // true = mỗi khách chỉ được dùng 1 lần
    @Column(name = "MOI_KHACH_1_LAN", nullable = false)
    private Boolean moiKhach1Lan = true;

    // ── Audit ────────────────────────────────────────────────
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
        if (daSuDung == null)    daSuDung    = 0;
        if (phamViApDung == null) phamViApDung = "ALL";
        if (doiTuong == null)    doiTuong    = "ALL";
        if (moiKhach1Lan == null) moiKhach1Lan = true;
    }

    @PreUpdate
    protected void onUpdate() {
        ngaySua = LocalDateTime.now();
    }
}