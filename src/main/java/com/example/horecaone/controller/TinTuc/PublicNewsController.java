package com.example.horecaone.controller.TinTuc;
import com.example.horecaone.entity.TinTuc.BaiViet;
import com.example.horecaone.repository.TinTuc.BaiVietRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/news")

public class PublicNewsController {

    @Autowired
    private BaiVietRepository baiVietRepository;

    // [GET] /api/news - Lấy danh sách tin tức
    @GetMapping
    public ResponseEntity<List<BaiViet>> getPublicNews() {
        List<BaiViet> newsList = baiVietRepository.findTop20ByTrangThaiOrderByNgayTaoDesc(true);
        return ResponseEntity.ok(newsList);
    }

    // 🔥 THÊM ĐOẠN NÀY: [GET] /api/news/{id} - Lấy chi tiết 1 bài viết
    @GetMapping("/{id}")
    public ResponseEntity<?> getNewsDetail(@PathVariable Long id) {
        return baiVietRepository.findById(id)
                .map(article -> {
                    // Tùy chọn: Tăng lượt xem mỗi khi có người nhấn vào xem
                    article.setLuotXem((article.getLuotXem() == null ? 0 : article.getLuotXem()) + 1);
                    baiVietRepository.save(article);

                    return ResponseEntity.ok(article);
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ── UPLOAD ẢNH ──────────────────────────────────────────
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
