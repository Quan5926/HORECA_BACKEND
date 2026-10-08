package com.example.horecaone.repository.categories;

import com.example.horecaone.entity.categories.DanhMuc;
import com.example.horecaone.dto.categories.DanhMucResponseDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {

    // BỔ SUNG d.danhMucChaId vào hàm khởi tạo DTO
    @Query("SELECT new com.example.horecaone.dto.categories.DanhMucResponseDTO(" +
            "d.id, d.tenDanhMuc, d.moTa, d.anhDaiDien, d.trangThai, " +
            "(SELECT COUNT(DISTINCT s.id) FROM SanPham s JOIN s.chiTietSanPhams ct " +
            " WHERE s.danhMuc.id = d.id AND s.trangThai = true AND ct.tonKho > 0), " +
            "d.danhMucChaId) " +
            "FROM DanhMuc d " +
            "ORDER BY d.id DESC")
    List<DanhMucResponseDTO> findAllWithProductCount();


    List<DanhMuc> findByDanhMucChaId(Long danhMucChaId);
}