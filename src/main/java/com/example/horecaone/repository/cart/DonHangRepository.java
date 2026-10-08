package com.example.horecaone.repository.cart;

import com.example.horecaone.entity.cart.DonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface DonHangRepository extends JpaRepository<DonHang, Long> {
    Optional<DonHang> findByMaDonHang(String maDonHang);

    List<DonHang> findByNguoiDungIdOrderByIdDesc(Long nguoiDungId);
}
