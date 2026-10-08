package com.example.horecaone.service.reviews;

import com.example.horecaone.dto.reviews.ReviewDTO;
import com.example.horecaone.entity.reviews.DanhGia;
import com.example.horecaone.repository.reviews.DanhGiaRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ReviewServiceImpl implements ReviewService {

    @Autowired
    private DanhGiaRepository danhGiaRepository;
    @Transactional
    @Override
    public List<ReviewDTO> getAllReviews() {
        return danhGiaRepository.findAllReviewsAdmin();
    }

    // BẠN TÌM HÀM updateStatus:
    @Transactional
    @Override
    public void updateStatus(Long id, Integer status) {
        DanhGia danhGia = danhGiaRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đánh giá/bình luận!"));

        // SỬA DÒNG SET DỮ LIỆU THÀNH NHƯ SAU:
        // Nếu status = 1 thì sẽ trả về true (Đã duyệt), ngược lại là false
        danhGia.setDaDuyet(status != null && status == 1);

        danhGiaRepository.save(danhGia);
    }
    @Transactional
    @Override
    public void deleteReview(Long id) {
        if (!danhGiaRepository.existsById(id)) {
            throw new RuntimeException("Đánh giá/bình luận không tồn tại!");
        }
        danhGiaRepository.deleteById(id);
    }
}