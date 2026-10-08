package com.example.horecaone.entity.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
@Entity
@Table(name = "CHI_TIET_COMBO")
@Data
public class ChiTietCombo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CHI_TIET_COMBO_ID")
    private ChiTietSanPham chiTietCombo;

    @Column(name = "TEN_COMBO_ITEM", length = 500)
    private String tenComboItem;

    @Column(name = "ANH_COMBO_ITEM", length = 1000)
    private String anhComboItem;

    @Column(name = "SO_LUONG")
    private Integer soLuong = 1;

    @Column(name = "TRANG_THAI")
    private Boolean trangThai = true;

    @Column(name = "NGUOI_TAO")
    private Long nguoiTao;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    @PrePersist
    public void prePersist() {
        ngayTao = LocalDateTime.now();
        if (trangThai == null) trangThai = true;
    }

    @PreUpdate
    public void preUpdate() {
        ngaySua = LocalDateTime.now();
    }
}