package com.example.horecaone.controller.TinTuc;
import com.example.horecaone.dto.TinTuc.NewsRequestDTO;
import com.example.horecaone.entity.TinTuc.BaiViet;
import com.example.horecaone.repository.TinTuc.BaiVietRepository;
import com.example.horecaone.service.TinTuc.BaiVietService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/news")
public class AdminNewsController {

    @Autowired
    private BaiVietService baiVietService;

    @PostMapping
    public ResponseEntity<?> createNews(@RequestBody NewsRequestDTO request) {
        try {
            // TODO: Ở đây bạn lấy ID User từ Spring Security Context (người đang đăng nhập)
            // Tạm thời tôi để hardcode ID = 3 (Admin Vipep theo DB của bạn)
            Long currentUserId = 3L;

            BaiViet savedNews = baiVietService.createNews(request, currentUserId);
            return ResponseEntity.status(HttpStatus.CREATED).body(savedNews);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi thêm bài viết: " + e.getMessage());
        }
    }
    @Autowired
    private BaiVietRepository baiVietRepository;

    // [GET] /api/admin/news - Lấy TẤT CẢ tin tức cho Admin quản lý
    @GetMapping
    public ResponseEntity<List<BaiViet>> getAllNews() {
        List<BaiViet> allNews = baiVietRepository.findAllByOrderByNgayTaoDesc();
        return ResponseEntity.ok(allNews);
    }

    // [PUT] /api/admin/news/{id} - SỬA BÀI VIẾT
    @PutMapping("/{id}")
    public ResponseEntity<?> updateNews(@PathVariable Long id, @RequestBody NewsRequestDTO request) {
        try {
            // TODO: Lấy ID từ Security Context. Tạm thời hardcode là 3L.
            Long currentUserId = 3L;

            BaiViet updatedNews = baiVietService.updateNews(id, request, currentUserId);
            return ResponseEntity.ok(updatedNews);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi cập nhật bài viết: " + e.getMessage());
        }
    }

    // [DELETE] /api/admin/news/{id} - XÓA BÀI VIẾT
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteNews(@PathVariable Long id) {
        try {
            baiVietService.deleteNews(id);
            return ResponseEntity.ok().body("Xóa bài viết thành công!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Lỗi khi xóa bài viết: " + e.getMessage());
        }
    }

    @PostMapping("/upload-image")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = "uploads/news/";
            new File(uploadDir).mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Files.write(Paths.get(uploadDir + fileName), file.getBytes());

            return ResponseEntity.ok(Map.of("url", "/uploads/news/" + fileName));
        } catch (Exception e) {
            return ResponseEntity.status(500).body("Upload thất bại: " + e.getMessage());
        }
    }
}
