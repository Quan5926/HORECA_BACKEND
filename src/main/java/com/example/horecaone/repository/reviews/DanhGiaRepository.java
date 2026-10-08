package com.example.horecaone.repository.reviews;

import com.example.horecaone.dto.reviews.ReviewDTO;
import com.example.horecaone.entity.reviews.DanhGia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhGiaRepository extends JpaRepository<DanhGia, Long> {

    // 1. API cho Admin (SỬA LẠI: Thêm d.hinhAnh vào cuối)
    @Query("SELECT new com.example.horecaone.dto.reviews.ReviewDTO(" +
            "d.id, " +                       // 1
            "d.loai, " +                     // 2
            "COALESCE(d.baiVietId, d.sanPhamId), " + // 3 (ID mục tiêu)
            "COALESCE(bv.tieuDe, sp.tenSanPham), " + // 4 (Tên mục tiêu)
            "u.hoTen, " +                    // 5
            "d.diemDanhGia, " +              // 6
            "d.noiDung, " +                  // 7
            "d.daDuyet, " +                  // 8
            "d.ngayTao, " +                  // 9
            "d.hinhAnh) " +                  // 10 (THÊM TRƯỜNG NÀY ĐỂ HẾT LỖI)
            "FROM DanhGia d " +
            "LEFT JOIN BaiViet bv ON d.baiVietId = bv.id " +
            "LEFT JOIN SanPham sp ON d.sanPhamId = sp.id " +
            "LEFT JOIN NguoiDung u ON d.nguoiDungId = u.id " +
            "ORDER BY d.ngayTao DESC")
    List<ReviewDTO> findAllReviewsAdmin();

    // 2. API lấy danh sách bình luận bài viết (Client) - (Bạn đã sửa đúng)
    @Query("SELECT new com.example.horecaone.dto.reviews.ReviewDTO(" +
            "d.id, d.loai, d.baiVietId, '', nd.hoTen, d.diemDanhGia, d.noiDung, d.daDuyet, d.ngayTao, d.hinhAnh) " +
            "FROM DanhGia d " +
            "LEFT JOIN NguoiDung nd ON d.nguoiDungId = nd.id " +
            "WHERE d.baiVietId = :baiVietId " +
            "AND d.daDuyet = true " +
            "AND d.loai = 'TIN_TUC' " +
            "ORDER BY d.ngayTao DESC")
    List<ReviewDTO> findApprovedReviewsByNewsId(@Param("baiVietId") Long baiVietId);


    // 3. API lấy danh sách đánh giá sản phẩm
    @Query("SELECT new com.example.horecaone.dto.reviews.ReviewDTO(" +
            "d.id, d.loai, d.sanPhamId, '', u.hoTen, d.diemDanhGia, d.noiDung, d.daDuyet, d.ngayTao, d.hinhAnh) " +
            "FROM DanhGia d " +
            "LEFT JOIN NguoiDung u ON d.nguoiDungId = u.id " +
            "WHERE d.sanPhamId = :sanPhamId " +
            "AND d.daDuyet = true " +
            "ORDER BY d.ngayTao DESC")
    List<ReviewDTO> findApprovedReviewsByProductId(@Param("sanPhamId") Long sanPhamId);

    // 4. Tính điểm trung bình sao
    @Query("SELECT AVG(d.diemDanhGia) FROM DanhGia d WHERE d.sanPhamId = :sanPhamId AND d.daDuyet = true")
    Double getAverageStarsByProductId(@Param("sanPhamId") Long sanPhamId);

    // 5. Đếm tổng số lượt đánh giá
    @Query("SELECT COUNT(d) FROM DanhGia d WHERE d.sanPhamId = :sanPhamId AND d.daDuyet = true")
    Long countTotalReviewsByProductId(@Param("sanPhamId") Long sanPhamId);


    @Query("SELECT COUNT(d) > 0 FROM DanhGia d WHERE d.nguoiDungId = :userId AND d.sanPhamId = :productId")
    boolean existsByNguoiDungIdAndSanPhamId(@Param("userId") Long userId, @Param("productId") Long productId);

    @Query("SELECT dg.sanPhamId, AVG(dg.diemDanhGia * 1.0) " +
            "FROM DanhGia dg " +
            "WHERE dg.sanPhamId IN :ids " +
            "GROUP BY dg.sanPhamId")
    List<Object[]> getAverageRatingsGroupByProductIds(@Param("ids") List<Long> ids);
}