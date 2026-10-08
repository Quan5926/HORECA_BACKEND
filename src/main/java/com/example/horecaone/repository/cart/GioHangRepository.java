package com.example.horecaone.repository.cart;

import com.example.horecaone.entity.cart.GioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface GioHangRepository extends JpaRepository<GioHang, Long> {
    Optional<GioHang> findByNguoiDungId(Long nguoiDungId);
    Optional<GioHang> findByMaPhien(String maPhien);
}
