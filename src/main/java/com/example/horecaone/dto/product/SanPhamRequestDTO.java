package com.example.horecaone.dto.product;

import lombok.Data;
import java.util.List;

@Data
public class SanPhamRequestDTO {
    // ── Thông tin SAN_PHAM ──
    private String tenSanPham;
    private String moTaNgan;
    private String moTaChiTiet;
    private Long danhMucId;
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

    // ── Dùng khi không có biến thể (sản phẩm đơn) ──
    private Long chiTietId;
    private String maSku;
    private String trongLuong;
    private Double giaNhap;
    private Double giaBan;
    private Integer tonKho;

    // ── Danh sách biến thể (khi có màu/size) ──
    private List<ChiTietSanPhamDTO> chiTietSanPhams;

    // ── Ảnh cũ giữ lại ──
    private List<String> danhSachAnhCu;
    // ── Liên hệ ──
    private Boolean lienHe;
    private String soDienThoai;
    private String zaloLink;
    private String mauSac;
    private String kichThuoc;
    private String mauSacHex;
    // Thêm field này vào class SanPhamRequestDTO
    private List<ChiTietComboDTO> chiTietCombos;
}