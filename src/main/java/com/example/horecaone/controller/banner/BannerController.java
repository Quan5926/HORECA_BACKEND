package com.example.horecaone.controller.banner;

import com.example.horecaone.dto.banner.BannerDTO;
import com.example.horecaone.service.banner.BannerService;
import com.example.horecaone.service.banner.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/banners")
@RequiredArgsConstructor
public class BannerController {

    private final BannerService bannerService;
    private final FileService fileService; // Spring sẽ tự inject nhờ @RequiredArgsConstructor

    // ================= API CHO FRONT-END USER =================
    // API: GET /api/banners/public?viTri=HOME
    @GetMapping("/public")
    public ResponseEntity<List<BannerDTO>> getPublicBanners(@RequestParam String viTri) {
        return ResponseEntity.ok(bannerService.getPublicBanners(viTri));
    }

    // ================= API CHO FRONT-END ADMIN =================
    // Lấy tất cả Banner
    @GetMapping
    public ResponseEntity<List<BannerDTO>> getAllBanners() {
        return ResponseEntity.ok(bannerService.getAllBannersAdmin());
    }

    // Thêm mới Banner
    @PostMapping
    public ResponseEntity<BannerDTO> createBanner(@RequestBody BannerDTO bannerDTO) {
        return ResponseEntity.ok(bannerService.createBanner(bannerDTO));
    }

    // Cập nhật Banner
    @PutMapping("/{id}")
    public ResponseEntity<BannerDTO> updateBanner(@PathVariable Long id, @RequestBody BannerDTO bannerDTO) {
        return ResponseEntity.ok(bannerService.updateBanner(id, bannerDTO));
    }

    // Xóa Banner
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBanner(@PathVariable Long id) {
        bannerService.deleteBanner(id);
        return ResponseEntity.ok("Xóa banner thành công!");
    }

    // Bật/Tắt trạng thái (Nút Switch gạt bên Admin)
    @PatchMapping("/{id}/toggle-status")
    public ResponseEntity<BannerDTO> toggleStatus(@PathVariable Long id) {
        return ResponseEntity.ok(bannerService.toggleStatus(id));
    }

    // ================= API UPLOAD ẢNH =================
    // Chỉ giữ lại 1 hàm duy nhất gọi qua FileService cho gọn
    @PostMapping("/upload")
    public ResponseEntity<String> uploadBannerImage(@RequestParam("file") MultipartFile file) {
        try {
            // Đổi từ saveFile sang storeFile(file, "banners")
            String fileUrl = fileService.storeFile(file, "banners");
            return ResponseEntity.ok(fileUrl);
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Lỗi khi tải ảnh lên: " + e.getMessage());
        }
    }
}
