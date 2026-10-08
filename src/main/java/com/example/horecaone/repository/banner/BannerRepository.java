package com.example.horecaone.repository.banner;

import com.example.horecaone.entity.banner.Banner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BannerRepository extends JpaRepository<Banner, Long> {

    // Dành cho FE User: Lấy banner ĐANG BẬT, theo VỊ TRÍ, sắp xếp theo THỨ TỰ TĂNG DẦN
    List<Banner> findByTrangThaiTrueAndViTriOrderByThuTuAsc(String viTri);

    // Dành cho Admin: Lấy tất cả theo vị trí (nếu cần)
    List<Banner> findByViTriOrderByThuTuAsc(String viTri);
}