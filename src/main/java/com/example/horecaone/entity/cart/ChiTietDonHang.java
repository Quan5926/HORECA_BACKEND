package com.example.horecaone.entity.cart;
import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
@Entity
@Table(name = "CHI_TIET_DON_HANG")
@Getter // Dùng cái này thay cho @Data
@Setter
public class ChiTietDonHang {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "DON_HANG_ID")
    @JsonIgnore
    private DonHang donHang;

    @Column(name = "CHI_TIET_SAN_PHAM_ID")
    private Long chiTietSanPhamId;

    @Column(name = "TEN_SAN_PHAM_LUC_MUA", length = 510)
    private String tenSanPhamLucMua;

    @Column(name = "THUOC_TINH_BIEN_THE")
    private String thuocTinhBienThe;

    @Column(name = "SO_LUONG")
    private Integer soLuong;

    @Column(name = "GIA_BAN")
    private BigDecimal giaBan;

    @Column(name = "THANH_TIEN")
    private BigDecimal thanhTien;

    // --- BỔ SUNG CÁC CỘT NÀY ĐỂ TRÁNH LỖI BÁO NULL TỪ DATABASE ---
    @Column(name = "NGUOI_TAO")
    private Long nguoiTao;

    @Column(name = "NGAY_TAO")
    private LocalDateTime ngayTao = LocalDateTime.now(); // Tự động lấy giờ hiện tại khi lưu

    @Column(name = "NGUOI_SUA")
    private Long nguoiSua;

    @Column(name = "NGAY_SUA")
    private LocalDateTime ngaySua;



    @jakarta.persistence.Transient
    private String tenSanPham;

    @jakarta.persistence.Transient
    private String anhSanPham;

    // --- TỰ VIẾT HÀM GET/SET LUÔN CHO CHẮC CÚ ---
    public String getTenSanPham() {
        return tenSanPham;
    }

    public void setTenSanPham(String tenSanPham) {
        this.tenSanPham = tenSanPham;
    }

    public String getAnhSanPham() {
        return anhSanPham;
    }

    public void setAnhSanPham(String anhSanPham) {
        this.anhSanPham = anhSanPham;
    }
    @Transient
    private Long sanPhamId; // 🔥 Bổ sung thêm dòng này

}
