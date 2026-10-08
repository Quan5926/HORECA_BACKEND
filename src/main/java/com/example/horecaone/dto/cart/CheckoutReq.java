package com.example.horecaone.dto.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class CheckoutReq {
    private Long nguoiDungId;
    private Long diaChiGiaoHangId;
    private String phuongThucThanhToan; // COD, VNPAY...
    private String ghiChu;
    private String maVoucher;
    private BigDecimal phiVanChuyen;
    // Danh sách các ID của chi tiết giỏ hàng được chọn để mua
    private List<Long> chiTietGioHangIds;
}
