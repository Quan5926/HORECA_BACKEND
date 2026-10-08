package com.example.horecaone.repository.address;

import com.example.horecaone.entity.address.DiaChiGiaoHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DiaChiGiaoHangRepository extends JpaRepository<DiaChiGiaoHang, Long> {

    // Lấy tất cả địa chỉ của 1 user
    List<DiaChiGiaoHang> findByNguoiDungId(Long nguoiDungId);

    // Hủy trạng thái mặc định của tất cả địa chỉ thuộc user này
    @Modifying
    @Query("UPDATE DiaChiGiaoHang d SET d.laDiaChiMacDinh = false WHERE d.nguoiDungId = :nguoiDungId")
    void resetDefaultAddressForUser(Long nguoiDungId);
}
