package com.example.horecaone.service.TinTuc;

import com.example.horecaone.dto.TinTuc.NewsRequestDTO;
import com.example.horecaone.entity.TinTuc.BaiViet;
import com.example.horecaone.repository.TinTuc.BaiVietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BaiVietService {

    @Autowired
    private BaiVietRepository baiVietRepository;

    public BaiViet createNews(NewsRequestDTO dto, Long currentUserId) {
        BaiViet baiViet = new BaiViet();

        // Map từ DTO sang Entity
        baiViet.setTieuDe(dto.getTitle());
        baiViet.setMoTaNgan(dto.getDesc());
        baiViet.setNoiDung(dto.getContent());
        baiViet.setAnhDaiDien(dto.getImage());
        baiViet.setDanhMuc(dto.getCategory());
        baiViet.setNoiBat(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);
        baiViet.setTags(dto.getTags());

        // THÊM DÒNG NÀY: Nhận trạng thái từ Frontend (true = Xuất bản, false = Lưu nháp)
        if (dto.getTrangThai() != null) {
            baiViet.setTrangThai(dto.getTrangThai());
        } else {
            baiViet.setTrangThai(false); // Nếu không gửi gì lên thì mặc định là bản nháp
        }

        // Lưu người tạo (lấy từ Token đăng nhập)
        baiViet.setNguoiTao(currentUserId);

        return baiVietRepository.save(baiViet);
    }

    // Hàm Cập nhật (Sửa) bài viết
    public BaiViet updateNews(Long id, NewsRequestDTO dto, Long currentUserId) {
        // 1. Tìm bài viết cũ trong Database
        BaiViet baiViet = baiVietRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy bài viết với ID: " + id));

        // 2. Cập nhật các trường dữ liệu mới từ DTO gửi lên
        baiViet.setTieuDe(dto.getTitle());
        baiViet.setMoTaNgan(dto.getDesc());
        baiViet.setNoiDung(dto.getContent());

        // Chỉ cập nhật ảnh nếu người dùng có gửi ảnh mới lên
        if (dto.getImage() != null && !dto.getImage().isEmpty()) {
            baiViet.setAnhDaiDien(dto.getImage());
        }

        baiViet.setDanhMuc(dto.getCategory());
        baiViet.setNoiBat(dto.getIsFeatured() != null ? dto.getIsFeatured() : false);
        baiViet.setTags(dto.getTags());

        // MỞ COMMENT VÀ THÊM DÒNG NÀY: Cập nhật trạng thái
        if (dto.getTrangThai() != null) {
            baiViet.setTrangThai(dto.getTrangThai());
        }

        // Cập nhật người sửa và ngày sửa
        baiViet.setNguoiSua(currentUserId);
        baiViet.setNgaySua(LocalDateTime.now());

        return baiVietRepository.save(baiViet);
    }

    // Hàm Xóa bài viết
    public void deleteNews(Long id) {
        if (!baiVietRepository.existsById(id)) {
            throw new RuntimeException("Bài viết không tồn tại!");
        }
        baiVietRepository.deleteById(id);
    }
}