package com.example.horecaone.entity.cart;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "DON_HANG")
@Getter // Dùng cái này thay cho @Data
@Setter
public class DonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NGUOI_DUNG_ID")
    private Long nguoiDungId;

    @Column(name = "MA_DON_HANG", length = 40)
    private String maDonHang;

    @Column(name = "TONG_TIEN_TRUOC_GIAM")
    private BigDecimal tongTienTruocGiam;

    @Column(name = "TIEN_GIAM_GIA")
    private BigDecimal tienGiamGia;

    @Column(name = "PHI_VAN_CHUYEN")
    private BigDecimal phiVanChuyen;

    @Column(name = "TONG_TIEN_THANH_TOAN")
    private BigDecimal tongTienThanhToan;

    // CHỖ NÀY ĐÃ ĐƯỢC SỬA: Thêm insertable = false, updatable = false
    @Column(name = "TONG_TIEN_SAUGIAM", insertable = false, updatable = false)
    private BigDecimal tongTienSauGiam;

    @Column(name = "TRANG_THAI", length = 60)
    private String trangThai;

    @Column(name = "PHUONG_THUC_THANH_TOAN", length = 60)
    private String phuongThucThanhToan;

    @Column(name = "TRANG_THAI_THANH_TOAN", length = 40)
    private String trangThaiThanhToan;

    @Column(name = "DIA_CHI_GIAO_HANG_ID")
    private Long diaChiGiaoHangId;

    @Column(name = "GHI_CHU")
    private String ghiChu;

    @Column(name = "MA_VOUCHER_AP_DUNG", length = 100)
    private String maVoucherApDung;

    // --- BỔ SUNG THÊM CÁC CỘT AUDIT TRONG DATABASE ĐỂ KHÔNG BỊ LỖI ---
    @Column(name = "NGUOI_TAO")
    private Long nguoiTao;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao = LocalDateTime.now(); // Tự động lấy giờ hiện tại

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    @OneToMany(mappedBy = "donHang", cascade = CascadeType.ALL)
    private List<ChiTietDonHang> chiTietDonHangs;
}