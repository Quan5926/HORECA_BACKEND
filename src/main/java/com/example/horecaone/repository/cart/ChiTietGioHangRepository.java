package com.example.horecaone.repository.cart;
import com.example.horecaone.entity.cart.ChiTietGioHang ;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;


import java.util.Optional;

@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Long> {
    // Tìm sản phẩm trong giỏ hàng để kiểm tra xem đã tồn tại chưa
    Optional<ChiTietGioHang> findByGioHangIdAndChiTietSanPhamId(Long gioHangId, Long chiTietSanPhamId);
}
