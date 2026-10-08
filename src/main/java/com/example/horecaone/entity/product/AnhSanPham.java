package com.example.horecaone.entity.product;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;

@Entity
@Table(name = "ANH_SAN_PHAM")
@Data
public class AnhSanPham {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "DUONG_DAN_ANH")
    private String duongDanAnh;

    @Column(name = "LA_ANH_CHINH")
    private Boolean laAnhChinh;

    @Column(name = "THU_TU")
    private Integer thuTu;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "SAN_PHAM_ID")
    @JsonBackReference
    private SanPham sanPham;
}