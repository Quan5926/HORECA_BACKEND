package com.example.horecaone.entity.categories;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "DANH_MUC")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMuc {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TEN_DANH_MUC", nullable = false, length = 100)
    private String tenDanhMuc;

    @Column(name = "DANH_MUC_CHA_ID")
    private Long danhMucChaId;

    @Column(name = "MO_TA", columnDefinition = "NVARCHAR(MAX)")
    private String moTa;

    @Column(name = "ANH_DAI_DIEN", length = 500)
    private String anhDaiDien;

    @Column(name = "TRANG_THAI", nullable = false)
    private Boolean trangThai;

    @Column(name = "NGUOI_TAO")
    private Long nguoiTao;

    @CreationTimestamp
    @Column(name = "NGAY_TAO", updatable = false)
    private LocalDateTime ngayTao;

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @UpdateTimestamp
    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;
}