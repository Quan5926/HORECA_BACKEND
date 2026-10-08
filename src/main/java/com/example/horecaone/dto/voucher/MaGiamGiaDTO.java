package com.example.horecaone.dto.voucher;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

public class MaGiamGiaDTO {

    /** Request: tạo / cập nhật voucher */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private String maVoucher;           // SUMMER2026
        private String loaiGiam;            // PHAN_TRAM | CO_DINH | FREESHIP
        private BigDecimal giaTriGiam;      // 0 nếu freeship
        private BigDecimal donToiThieu;
        private BigDecimal giamToiDa;       // null = không giới hạn
        private Integer gioiHanSuDung;      // null = unlimited
        private LocalDateTime ngayBatDau;
        private LocalDateTime ngayKetThuc;
        private Boolean trangThaiHoatDong;

        // ── Field mới ──────────────────────────────────────
        private String moTa;                // Tên chương trình voucher
        private String phamViApDung;        // ALL | CATEGORY | SPECIFIC
        private String doiTuong;            // ALL | NEW | VIP
        private Boolean moiKhach1Lan;       // true = mỗi khách chỉ dùng 1 lần

        private Long nguoiTao;
        private Long nguoiSua;
    }

    /** Response: trả về FE */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private String maVoucher;
        private String loaiGiam;
        private BigDecimal giaTriGiam;
        private BigDecimal donToiThieu;
        private BigDecimal giamToiDa;
        private Integer gioiHanSuDung;
        private Integer daSuDung;
        private LocalDateTime ngayBatDau;
        private LocalDateTime ngayKetThuc;
        private Boolean trangThaiHoatDong;
        private String trangThaiHienThi;    // Đang diễn ra | Sắp tới | Đã hết hạn | Tạm dừng | Hết lượt

        // ── Field mới ──────────────────────────────────────
        private String moTa;
        private String phamViApDung;
        private String doiTuong;
        private Boolean moiKhach1Lan;

        private LocalDateTime ngayTao;
        private LocalDateTime ngaySua;
    }

    /** Request dùng khi khách áp mã ở checkout */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor
    public static class ApplyRequest {
        private String maVoucher;
        private BigDecimal tongDonHang;
    }

    /** Response trả về số tiền được giảm */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class ApplyResponse {
        private Long maGiamGiaId;
        private String maVoucher;
        private String loaiGiam;
        private BigDecimal soTienGiam;
        private String thongBao;
    }
}