package com.example.horecaone.dto.product;

import com.example.horecaone.dto.reviews.ReviewDTO;
import lombok.Data;
import java.util.List;

@Data
public class SanPhamDetailDTO {
    private Long id;
    private String tenSanPham;
    private String moTaNgan;
    private String moTaChiTiet;
    private Long danhMucId;
    private String tenDanhMuc;
    private Boolean trangThai;
    private Boolean laCombo;

    // ── Thông tin bổ sung ──
    private String thuongHieu;
    private String chatLieu;
    private String kieuDang;
    private String chatLuong;
    private String xuatXu;
    private String tronBo;
    private String phuHopCho;

    // ── Ảnh sản phẩm ──
    private String anhChinh;
    private List<String> danhSachAnh;

    // ── Chi tiết đơn lẻ (giữ lại để tương thích giỏ hàng) ──
    private Long chiTietId;
    private String maSku;
    private String trongLuong;
    private Double giaNhap;
    private Double giaBan;
    private Integer tonKho;

    // ── Danh sách biến thể (màu + size kiểu Shopee) ──
    private List<ChiTietSanPhamDTO> chiTietSanPhams;

    // ── Thống kê ──
    private Integer daBan;
    private Double diemDanhGia;
    private Long soLuotDanhGia;
    private List<ReviewDTO> reviews;
    // ── Liên hệ ──
    private Boolean lienHe;
    private String soDienThoai;
    private String zaloLink;


    // Thêm field này vào class SanPhamDetailDTO
    private List<ChiTietComboDTO> chiTietCombos;
}