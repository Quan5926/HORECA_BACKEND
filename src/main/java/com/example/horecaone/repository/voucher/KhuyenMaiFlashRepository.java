package com.example.horecaone.repository.voucher;

import com.example.horecaone.entity.voucher.KhuyenMaiFlash;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface KhuyenMaiFlashRepository extends JpaRepository<KhuyenMaiFlash, Long> {

    // Lấy tất cả flash sale theo trạng thái
    List<KhuyenMaiFlash> findByTrangThai(Boolean trangThai);

    // Lấy flash sale đang hoạt động của 1 sản phẩm (dùng khi tính giá)
    @Query("""
        SELECT k FROM KhuyenMaiFlash k
        WHERE k.chiTietSanPhamId = :chiTietSanPhamId
          AND k.trangThai = true
          AND k.ngayBatDau <= :now
          AND k.ngayKetThuc >= :now
        ORDER BY k.phanTramGiamGia DESC
    """)
    Optional<KhuyenMaiFlash> findActiveByChiTietSanPhamId(
            @Param("chiTietSanPhamId") Long chiTietSanPhamId,
            @Param("now") LocalDateTime now
    );

    // Lấy tất cả flash sale của 1 sản phẩm (admin xem lịch sử)
    List<KhuyenMaiFlash> findByChiTietSanPhamIdOrderByNgayTaoDesc(Long chiTietSanPhamId);

    // Kiểm tra overlap thời gian cho cùng sản phẩm (tránh tạo 2 flash sale cùng lúc)
    @Query("""
        SELECT COUNT(k) > 0 FROM KhuyenMaiFlash k
        WHERE k.chiTietSanPhamId = :chiTietSanPhamId
          AND k.trangThai = true
          AND k.id <> :excludeId
          AND k.ngayBatDau < :ngayKetThuc
          AND k.ngayKetThuc > :ngayBatDau
    """)
    boolean existsOverlap(
            @Param("chiTietSanPhamId") Long chiTietSanPhamId,
            @Param("ngayBatDau") LocalDateTime ngayBatDau,
            @Param("ngayKetThuc") LocalDateTime ngayKetThuc,
            @Param("excludeId") Long excludeId
    );
}