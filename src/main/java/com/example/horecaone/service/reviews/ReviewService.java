package com.example.horecaone.service.reviews;

import com.example.horecaone.dto.reviews.ReviewDTO;

import java.util.List;

public interface ReviewService {
    List<ReviewDTO> getAllReviews();
    void updateStatus(Long id, Integer status);
    void deleteReview(Long id);
}