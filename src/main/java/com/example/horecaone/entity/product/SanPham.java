package com.example.horecaone.entity.product;

import com.example.horecaone.entity.categories.DanhMuc;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "SAN_PHAM")
@Data
public class SanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TEN_SAN_PHAM")
    private String tenSanPham;

    @Column(name = "MO_TA_NGAN", length = 1000)
    private String moTaNgan;

    @Column(name = "MO_TA_CHI_TIET", columnDefinition = "NVARCHAR(MAX)")
    private String moTaChiTiet;

    @Column(name = "TRANG_THAI")
    private Boolean trangThai = true;

    @Column(name = "LA_COMBO")
    private Boolean laCombo = false;

    // ── Thông tin bổ sung ──
    @Column(name = "THUONG_HIEU", length = 400)
    private String thuongHieu;

    @Column(name = "CHAT_LIEU", length = 400)
    private String chatLieu;

    @Column(name = "KIEU_DANG", length = 400)
    private String kieuDang;

    @Column(name = "CHAT_LUONG", length = 400)
    private String chatLuong;

    @Column(name = "XUAT_XU", length = 400)
    private String xuatXu;

    // ── Cần ALTER TABLE thêm 2 cột này ──
    @Column(name = "TRON_BO", length = 500)
    private String tronBo;

    @Column(name = "PHU_HOP_CHO", length = 400)
    private String phuHopCho;

    // ── Quan hệ ──
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "DANH_MUC_ID")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private DanhMuc danhMuc;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<ChiTietSanPham> chiTietSanPhams;

    @OneToMany(mappedBy = "sanPham", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonManagedReference
    private List<AnhSanPham> anhSanPhams;


    @Column(name = "LIEN_HE")
    private Boolean lienHe = false;

    @Column(name = "SO_DIEN_THOAI", length = 20)
    private String soDienThoai;

    @Column(name = "ZALO_LINK", length = 100)
    private String zaloLink;
}