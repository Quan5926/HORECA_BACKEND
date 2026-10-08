package com.example.horecaone.controller.reviews;

import com.example.horecaone.dto.reviews.ReviewDTO;
import com.example.horecaone.service.reviews.ReviewService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/reviews")
public class ReviewAdminController {

    @Autowired
    private ReviewService reviewService;

    // 1. Lấy danh sách đánh giá & bình luận
    @GetMapping
    public ResponseEntity<List<ReviewDTO>> getAllReviews() {
        List<ReviewDTO> reviews = reviewService.getAllReviews();
        return ResponseEntity.ok(reviews);
    }

    // 2. Phê duyệt hoặc Ẩn
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestParam Integer status) {
        try {
            reviewService.updateStatus(id, status);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Cập nhật trạng thái thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    // 3. Xóa
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteReview(@PathVariable Long id) {
        try {
            reviewService.deleteReview(id);
            Map<String, String> response = new HashMap<>();
            response.put("message", "Xóa thành công!");
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}