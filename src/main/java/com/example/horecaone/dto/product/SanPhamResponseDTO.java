package com.example.horecaone.dto.product;

import lombok.Data;

@Data
public class SanPhamResponseDTO {
    private Long id;
    private String tenSanPham;
    private String moTaNgan;
    private Boolean laCombo;

    // ── Giá (lấy từ biến thể rẻ nhất hoặc SP đơn) ──
    private Long chiTietId;
    private String maSku;
    private Double giaBan;
    private Double giaNhap;
    private Integer tonKho;

    // ── Ảnh ──
    private String anhChinh;

    // ── Danh mục ──
    private String danhMuc;

    // ── Trạng thái & thống kê ──
    private Boolean trangThai;
    private Integer daBan;
    private Double diemDanhGia;
    private Integer soLuongBienThe;
    private Double  giaBanMax;
    private Boolean lienHe;
    private String soDienThoai;
    private String zaloLink;


    private Double giaGoc; // giá gốc trước khi giảm
}