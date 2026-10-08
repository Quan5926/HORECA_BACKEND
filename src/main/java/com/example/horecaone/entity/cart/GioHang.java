package com.example.horecaone.entity.cart;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "GIO_HANG")
@Data
public class GioHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "NGUOI_DUNG_ID")
    private Long nguoiDungId;

    @Column(name = "MA_PHIEN", length = 200)
    private String maPhien;

    @Column(name = "MA_VOUCHER_AP_DUNG", length = 100)
    private String maVoucherApDung;

    // Bỏ qua các trường Audit (NguoiTao, NgayTao...) để code gọn nhé

    @OneToMany(mappedBy = "gioHang", cascade = CascadeType.ALL)
    private List<ChiTietGioHang> chiTietGioHangs;
}
