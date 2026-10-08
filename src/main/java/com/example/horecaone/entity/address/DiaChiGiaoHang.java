package com.example.horecaone.entity.address;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "DIA_CHI_GIAO_HANG") // Sửa lại tên bảng thật trong DB của bạn nhé
public class DiaChiGiaoHang {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "NGUOI_DUNG_ID", nullable = false)
    private Long nguoiDungId;

    @Column(name = "HO_TEN_NGUOI_NHAN", length = 200, nullable = false)
    private String hoTenNguoiNhan;

    @Column(name = "SO_DIEN_THOAI", length = 40, nullable = false)
    private String soDienThoai;

    @Column(name = "DIA_CHI_DAY_DU", length = 1000, nullable = false)
    private String diaChiDayDu;

    @Column(name = "LA_DIA_CHI_MAC_DINH", nullable = false)
    private Boolean laDiaChiMacDinh;

    @Column(name = "NGUOI_TAO", nullable = false)
    private Long nguoiTao;

    @Column(name = "NGAY_TAO", nullable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;
}