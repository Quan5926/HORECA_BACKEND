package com.example.horecaone.entity.reviews;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Entity
@Table(name = "DANH_GIA")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class DanhGia {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "SAN_PHAM_ID")
    private Long sanPhamId; // Numeric -> Long

    @Column(name = "NGUOI_DUNG_ID")
    private Long nguoiDungId; // Numeric -> Long

    @Column(name = "DIEM_DANH_GIA")
    private Integer diemDanhGia; // Tinyint -> Integer

    @Column(name = "NOI_DUNG", columnDefinition = "NVARCHAR(MAX)")
    private String noiDung; // Nvarchar -> String

    @Column(name = "DA_DUYET")
    private Boolean daDuyet; // ĐỔI SANG BOOLEAN vì DB là kiểu BIT

    @Column(name = "NGUOI_TAO")
    private Long nguoiTao;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;

    @Column(name = "LOAI")
    private String loai; // Nvarchar -> String

    @Column(name = "BAI_VIET_ID")
    private Long baiVietId; // Numeric -> Long


    @Column(name = "HINH_ANH", columnDefinition = "NVARCHAR(MAX)")
    private String hinhAnh;
}