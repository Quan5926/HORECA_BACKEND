package com.example.horecaone.dto.voucher;

import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// ============================================================
//  DTO — KHUYEN_MAI_FLASH
// ============================================================

public class KhuyenMaiFlashDTO {

    /** Request: tạo / cập nhật flash sale */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Request {
        private Long chiTietSanPhamId;   // bắt buộc
        private Short phanTramGiamGia;   // 1–100
        private LocalDateTime ngayBatDau;
        private LocalDateTime ngayKetThuc;
        private Boolean trangThai;       // null → mặc định true
        private Long nguoiTao;
        private Long nguoiSua;
    }

    /** Response: trả về FE admin */
    @Getter @Setter @NoArgsConstructor @AllArgsConstructor @Builder
    public static class Response {
        private Long id;
        private Long chiTietSanPhamId;
        private Short phanTramGiamGia;
        private LocalDateTime ngayBatDau;
        private LocalDateTime ngayKetThuc;
        private Boolean trangThai;
        private String trangThaiHienThi;  // "Đang diễn ra" | "Sắp tới" | "Đã kết thúc"
        private LocalDateTime ngayTao;
        private LocalDateTime ngaySua;
    }
}
