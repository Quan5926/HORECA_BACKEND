package com.example.horecaone.dto.cart;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class CartItemResponseDTO {
    private Long id;
    private Long chiTietId;
    private String tenSanPham;
    private BigDecimal giaBan;
    private BigDecimal giaGoc;
    private Integer soLuong;
    private Integer tonKho;
    private String trongLuong;
    private String anhSanPham;
    private Boolean duocChon;
    private Long sanPhamId;
    // ← THÊM VÀO
    private String mauSac;
    private String kichThuoc;
    private String maSku;
}