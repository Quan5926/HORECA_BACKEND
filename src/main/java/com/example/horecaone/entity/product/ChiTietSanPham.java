package com.example.horecaone.entity.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "CHI_TIET_SAN_PHAM")
@Data
public class ChiTietSanPham {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "MA_SKU", unique = true, length = 200)
    private String maSku;

    @Column(name = "MAU_SAC", length = 200)
    private String mauSac;

    @Column(name = "KICH_THUOC", length = 200)
    private String kichThuoc;

    @Column(name = "TRONG_LUONG", length = 100)
    private String trongLuong;

    @Column(name = "GIA_NHAP")
    private Double giaNhap;

    @Column(name = "GIA_BAN")
    private Double giaBan;

    @Column(name = "TON_KHO")
    private Integer tonKho;

    @Column(name = "ANH_SAN_PHAM", length = 1000)
    private String anhSanPham;

    @Column(name = "TRANG_THAI")
    private Boolean trangThai = true;

    // ── Cần ALTER TABLE thêm cột này ──
    @Column(name = "MAU_SAC_HEX", length = 20)
    private String mauSacHex;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SAN_PHAM_ID")
    @JsonBackReference
    private SanPham sanPham;

    // ChiTietSanPham.java
    @Column(name = "gia_goc")
    private Double giaGoc;

}