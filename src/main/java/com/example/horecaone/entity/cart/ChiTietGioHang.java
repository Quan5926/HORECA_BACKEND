package com.example.horecaone.entity.cart;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@Entity
@Table(name = "CHI_TIET_GIO_HANG")
@Data
public class ChiTietGioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "GIO_HANG_ID")
    @JsonIgnore // THÊM DÒNG NÀY VÀO ĐỂ CẮT VÒNG LẶP
    private GioHang gioHang;

    @Column(name = "CHI_TIET_SAN_PHAM_ID")
    private Long chiTietSanPhamId;

    @Column(name = "SO_LUONG")
    private Integer soLuong;

    @Column(name = "GIA_KHI_THEM")
    private BigDecimal giaKhiThem;

    @Column(name = "DUOC_CHON")
    private Boolean duocChon;

    @Column(name = "GHI_CHU_KHACH", length = 510)
    private String ghiChuKhach;
}