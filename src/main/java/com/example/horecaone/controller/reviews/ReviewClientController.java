package com.example.horecaone.controller.reviews;


import com.example.horecaone.dto.reviews.ReviewDTO;
import com.example.horecaone.entity.reviews.DanhGia;
import com.example.horecaone.repository.reviews.DanhGiaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/reviews")

public class ReviewClientController {

    @Autowired
    private DanhGiaRepository danhGiaRepository;

    // 1. Lấy danh sách bình luận (Đã duyệt) của 1 bài viết
    @GetMapping("/news/{newsId}")
    public ResponseEntity<List<ReviewDTO>> getNewsReviews(@PathVariable Long newsId) {
        List<ReviewDTO> reviews = danhGiaRepository.findApprovedReviewsByNewsId(newsId);
        return ResponseEntity.ok(reviews);
    }

    @PostMapping("/news")
    public ResponseEntity<?> submitNewsReview(@RequestBody Map<String, Object> payload) {
        try {
            DanhGia newReview = new DanhGia();

            // 1. Lấy newsId từ payload
            Object newsIdObj = payload.get("newsId");
            if (newsIdObj == null) return ResponseEntity.badRequest().body("Thiếu ID bài viết");
            newReview.setBaiVietId(Long.valueOf(newsIdObj.toString()));

            // 2. Lấy userId từ payload (Frontend gửi lên)
            // Thay vì fix 1L, ta lấy từ payload. Nếu không có mới dự phòng là 38L
            Object userIdObj = payload.get("userId");
            if (userIdObj != null) {
                newReview.setNguoiDungId(Long.valueOf(userIdObj.toString()));
            } else {
                // Tạm thời để 38L (Khách Hàng Test 2) để tránh hiện Nguyễn Văn A
                // Sau này khi Frontend đã gửi userId thì dòng này sẽ không bị chạy vào
                newReview.setNguoiDungId(38L);
            }

            // 3. Các thông tin khác
            newReview.setSanPhamId(null);
            Object rating = payload.get("rating");
            newReview.setDiemDanhGia(rating != null ? Integer.valueOf(rating.toString()) : 5);
            newReview.setNoiDung(payload.get("content") != null ? payload.get("content").toString() : "");

            newReview.setLoai("TIN_TUC");
            newReview.setDaDuyet(false);
            newReview.setNgayTao(LocalDateTime.now());
            newReview.setNgaySua(LocalDateTime.now());

            danhGiaRepository.save(newReview);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Đã gửi bình luận!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    // -------------------------------------------------------------
    // THÊM MỚI: API dành riêng để nhận lời nhắn từ Đơn Hàng
    // -------------------------------------------------------------
    @PostMapping("/order")
    public ResponseEntity<?> submitOrderNote(@RequestBody Map<String, Object> payload) {
        try {
            DanhGia newReview = new DanhGia();

            Object userIdObj = payload.get("userId");
            if (userIdObj != null) {
                newReview.setNguoiDungId(Long.valueOf(userIdObj.toString()));
            } else {
                newReview.setNguoiDungId(38L);
            }

            // Gán null để không dính lỗi Khóa ngoại (không dính vào bài viết nào)
            newReview.setBaiVietId(null);
            newReview.setSanPhamId(null);

            newReview.setNoiDung(payload.get("content") != null ? payload.get("content").toString() : "");

            // Dùng TIN_TUC để vượt qua lỗi CHECK CONSTRAINT của Database
            newReview.setLoai("TIN_TUC");
            newReview.setDiemDanhGia(5);
            newReview.setDaDuyet(false);
            newReview.setNgayTao(LocalDateTime.now());
            newReview.setNgaySua(LocalDateTime.now());

            danhGiaRepository.save(newReview);
            return ResponseEntity.ok(Map.of("status", "success", "message", "Đã gửi lời nhắn đơn hàng!"));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi: " + e.getMessage());
        }
    }

    @PostMapping("/product")
    public ResponseEntity<?> submitProductReview(
            @RequestParam("productId") Long productId,
            @RequestParam(value = "userId", required = false, defaultValue = "38") Long userId,
            @RequestParam(value = "rating", required = false, defaultValue = "5") Integer rating,
            @RequestParam(value = "content", required = false, defaultValue = "") String content,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            DanhGia newReview = new DanhGia();

            // 1. Gán thông tin cơ bản
            newReview.setSanPhamId(productId);
            newReview.setNguoiDungId(userId);
            newReview.setBaiVietId(null);
            newReview.setLoai("SAN_PHAM");
            newReview.setDiemDanhGia(rating);
            newReview.setNoiDung(content);
            newReview.setDaDuyet(false);
            newReview.setNgayTao(LocalDateTime.now());
            newReview.setNgaySua(LocalDateTime.now());

            // 2. XỬ LÝ LƯU FILE (ẢNH / VIDEO) VÀ NỐI CHUỖI
            if (files != null && !files.isEmpty()) {
                String uploadDir = "uploads/reviews/";
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }

                // Dùng List để tạm chứa các tên file
                List<String> savedFileNames = new java.util.ArrayList<>();

                for (MultipartFile file : files) {
                    if (!file.isEmpty()) {
                        String fileName = UUID.randomUUID() + "_" + file.getOriginalFilename();
                        Path filePath = uploadPath.resolve(fileName);

                        Files.copy(file.getInputStream(), filePath);

                        System.out.println("Đã lưu file: " + filePath.toString());

                        // Thêm đường dẫn file vào List
                        savedFileNames.add("/uploads/reviews/" + fileName);
                    }
                }

                // Nếu có file được lưu, ghép chúng lại thành chuỗi (ngăn cách bằng dấu phẩy)
                if (!savedFileNames.isEmpty()) {
                    String hinhAnhStr = String.join(",", savedFileNames);
                    newReview.setHinhAnh(hinhAnhStr); // <-- Gán vào Entity (Bảng DANH_GIA)
                }
            }

            // 3. Lưu toàn bộ xuống DB (chỉ gọi 1 lần save để tối ưu)
            danhGiaRepository.save(newReview);

            return ResponseEntity.ok(Map.of("status", "success", "message", "Đã gửi đánh giá sản phẩm!"));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of("status", "error", "message", "Lỗi: " + e.getMessage()));
        }
    }

    @GetMapping("/product/{productId}")
    public ResponseEntity<List<ReviewDTO>> getProductReviews(@PathVariable Long productId) {
        // Phải truyền productId vào hàm Repository
        List<ReviewDTO> reviews = danhGiaRepository.findApprovedReviewsByProductId(productId);
        return ResponseEntity.ok(reviews);
    }
}
