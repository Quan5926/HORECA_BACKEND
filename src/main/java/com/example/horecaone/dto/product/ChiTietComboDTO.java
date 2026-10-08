package com.example.horecaone.dto.product;

import lombok.Data;

@Data
public class ChiTietComboDTO {
    private Long id;
    private Long chiTietSanPhamId;
    private Long sanPhamId;        // thêm
    private String tenSanPham;
    private String anhSanPham;
    private String mauSac;         // thêm
    private String kichThuoc;      // thêm
    private Integer soLuong;
    private Boolean trangThai;
}