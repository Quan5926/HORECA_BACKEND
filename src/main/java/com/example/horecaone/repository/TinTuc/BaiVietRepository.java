package com.example.horecaone.repository.TinTuc;

import com.example.horecaone.entity.TinTuc.BaiViet;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface BaiVietRepository extends JpaRepository<BaiViet, Long> {

    // Dành cho Admin: Lấy tất cả bài viết (Admin cần quản lý thì lấy hết, nhưng cẩn thận mốt nhiều bài sẽ lag)
    List<BaiViet> findAllByOrderByNgayTaoDesc();

    // Dành cho User: ĐÃ SỬA LẠI THÊM CHỮ "Top20"
    // Chỉ lấy 20 bài viết mới nhất đang Active. Như vậy dữ liệu trả về cực nhẹ, không bao giờ bị Timeout!
    List<BaiViet> findTop20ByTrangThaiOrderByNgayTaoDesc(Boolean trangThai);
}