package com.example.horecaone.dto.cart;

import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class AddToCartReq {
    private Long nguoiDungId; // Cố định null nếu khách vãng lai
    private String maPhien;   // Dùng cho khách chưa đăng nhập
    private Long chiTietSanPhamId;
    private Integer soLuong;
    private BigDecimal giaHienTai;
}
