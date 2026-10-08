package com.example.horecaone.repository.voucher;

import com.example.horecaone.entity.voucher.MaGiamGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface MaGiamGiaRepository extends JpaRepository<MaGiamGia, Long> {

    // Tìm theo mã code (khách nhập khi checkout)
    Optional<MaGiamGia> findByMaVoucherIgnoreCase(String maVoucher);

    // Kiểm tra mã đã tồn tại chưa (tránh trùng khi tạo)
    boolean existsByMaVoucherIgnoreCase(String maVoucher);

    // Lọc theo trạng thái hoạt động
    List<MaGiamGia> findByTrangThaiHoatDongOrderByNgayTaoDesc(Boolean trangThai);

    // Tìm tất cả còn hiệu lực (dùng cho dropdown FE user)
    @Query("""
        SELECT m FROM MaGiamGia m
        WHERE m.trangThaiHoatDong = true
          AND (m.ngayBatDau IS NULL OR m.ngayBatDau <= :now)
          AND (m.ngayKetThuc IS NULL OR m.ngayKetThuc >= :now)
          AND (m.gioiHanSuDung IS NULL OR m.daSuDung < m.gioiHanSuDung)
        ORDER BY m.ngayTao DESC
    """)
    List<MaGiamGia> findAllValid(@Param("now") LocalDateTime now);

    // Tăng DA_SU_DUNG sau khi áp dụng thành công (atomic)
    @Modifying
    @Query("UPDATE MaGiamGia m SET m.daSuDung = m.daSuDung + 1 WHERE m.id = :id")
    void incrementDaSuDung(@Param("id") Long id);
}