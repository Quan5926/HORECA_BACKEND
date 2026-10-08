package com.example.horecaone.repository.cart;

import com.example.horecaone.entity.cart.ChiTietDonHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ChiTietDonHangRepository extends JpaRepository<ChiTietDonHang, Long> {

    @Query("SELECT COALESCE(SUM(ct.soLuong), 0) FROM ChiTietDonHang ct " +
            "JOIN ct.donHang dh " +
            "WHERE ct.chiTietSanPhamId IN (SELECT spct.id FROM ChiTietSanPham spct WHERE spct.sanPham.id = :sanPhamId) " +
            "AND dh.trangThai = 'HOAN_THANH'")
    Integer countTotalSoldBySanPhamId(@Param("sanPhamId") Long sanPhamId);


    @Query("SELECT ct.sanPham.id, SUM(cdh.soLuong) " +
            "FROM ChiTietDonHang cdh " +
            "JOIN ChiTietSanPham ct ON ct.id = cdh.chiTietSanPhamId " +
            "WHERE ct.sanPham.id IN :ids " +
            "GROUP BY ct.sanPham.id")
    List<Object[]> countSoldGroupByProductIds(@Param("ids") List<Long> ids);
}