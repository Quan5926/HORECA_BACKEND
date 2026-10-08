package com.example.horecaone.dto.product;

import lombok.Data;

@Data
public class ChiTietSanPhamDTO {
    private Long id;           // null khi tạo mới, có giá trị khi update
    private String maSku;
    private String mauSac;
    private String mauSacHex;  // Màu hex từ color picker (#ffffff...)
    private String kichThuoc;
    private String trongLuong;
    private Double giaNhap;
    private Double giaBan;
    private Integer tonKho;
    private String anhSanPham; // URL ảnh cũ nếu có (khi edit)
    private Boolean trangThai;
    private Double giaGoc;
}