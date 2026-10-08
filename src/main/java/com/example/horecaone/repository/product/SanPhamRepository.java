package com.example.horecaone.repository.product;

import com.example.horecaone.entity.product.SanPham;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;

@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    // ===================================================================
    // 1. TÌM KIẾM THEO DANH MỤC
    // ===================================================================

    // Lấy sản phẩm đang hoạt động theo ID danh mục (không phân trang)
    List<SanPham> findByDanhMucIdAndTrangThaiTrue(Long danhMucId);

    // ===================================================================
    // 2. TÌM KIẾM / LỌC CHO TRANG QUẢN LÝ (Admin)
    // ===================================================================

    @Query("SELECT s FROM SanPham s " +
            "WHERE (:search IS NULL OR :search = '' " +
            "      OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :search, '%')) " +
            "      OR EXISTS (SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s " +
            "                 AND LOWER(ct.maSku) LIKE LOWER(CONCAT('%', :search, '%')))) " +
            "AND (:#{#categoryIds == null} = true OR s.danhMuc.id IN :categoryIds) " +
            "AND (:trangThai IS NULL OR s.trangThai = :trangThai) " +
            "AND (:minPrice IS NULL OR EXISTS (" +
            "    SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan >= :minPrice)) " +
            "AND (:maxPrice IS NULL OR EXISTS (" +
            "    SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan <= :maxPrice))")
    Page<SanPham> searchProducts(
            @Param("search") String search,
            @Param("categoryIds") Set<Long> categoryIds,
            @Param("trangThai") Boolean trangThai,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // ===================================================================
    // 3. TÌM KIẾM NHANH CHO HEADER SEARCH (Khách hàng)
    // Chỉ lấy sản phẩm đang active, tìm theo tên hoặc mã SKU
    // ===================================================================

    @Query("SELECT DISTINCT s FROM SanPham s " +
            "LEFT JOIN s.chiTietSanPhams ct " +
            "WHERE s.trangThai = true " +
            "AND (:keyword IS NULL OR :keyword = '' " +
            "    OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "    OR LOWER(ct.maSku) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<SanPham> searchByKeyword(
            @Param("keyword") String keyword,
            Pageable pageable
    );

    // ===================================================================
    // 4. SẢN PHẨM LIÊN QUAN (cùng danh mục, khác ID hiện tại, đang active)
    // ===================================================================

    @Query("SELECT s FROM SanPham s " +
            "WHERE s.danhMuc.id = :categoryId " +
            "AND s.id != :currentProductId " +
            "AND s.trangThai = true " +
            "ORDER BY s.id DESC")
    List<SanPham> findRelatedProducts(
            @Param("categoryId") Long categoryId,
            @Param("currentProductId") Long currentProductId,
            Pageable pageable
    );

    // Sort giá ASC
    @Query("SELECT s FROM SanPham s " +
            "WHERE (:search IS NULL OR :search = '' " +
            "      OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:#{#categoryIds == null} = true OR s.danhMuc.id IN :categoryIds) " +
            "AND (:trangThai IS NULL OR s.trangThai = :trangThai) " +
            "AND (:minPrice IS NULL OR EXISTS (" +
            "    SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan >= :minPrice)) " +
            "AND (:maxPrice IS NULL OR EXISTS (" +
            "    SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan <= :maxPrice)) " +
            "ORDER BY (SELECT MIN(ct2.giaBan) FROM ChiTietSanPham ct2 WHERE ct2.sanPham = s) ASC")
    Page<SanPham> searchProductsSortByPriceAsc(
            @Param("search") String search,
            @Param("categoryIds") Set<Long> categoryIds,
            @Param("trangThai") Boolean trangThai,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // Sort giá DESC
    @Query("SELECT s FROM SanPham s " +
            "WHERE (:search IS NULL OR :search = '' " +
            "      OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:#{#categoryIds == null} = true OR s.danhMuc.id IN :categoryIds) " +
            "AND (:trangThai IS NULL OR s.trangThai = :trangThai) " +
            "AND (:minPrice IS NULL OR EXISTS (" +
            "    SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan >= :minPrice)) " +
            "AND (:maxPrice IS NULL OR EXISTS (" +
            "    SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan <= :maxPrice)) " +
            "ORDER BY (SELECT MIN(ct2.giaBan) FROM ChiTietSanPham ct2 WHERE ct2.sanPham = s) DESC")
    Page<SanPham> searchProductsSortByPriceDesc(
            @Param("search") String search,
            @Param("categoryIds") Set<Long> categoryIds,
            @Param("trangThai") Boolean trangThai,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );

    // Sort bán chạy
    @Query("SELECT s FROM SanPham s " +
            "WHERE (:search IS NULL OR :search = '' " +
            "      OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:#{#categoryIds == null} = true OR s.danhMuc.id IN :categoryIds) " +
            "AND (:trangThai IS NULL OR s.trangThai = :trangThai) " +
            "AND (:minPrice IS NULL OR EXISTS (" +
            "    SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan >= :minPrice)) " +
            "AND (:maxPrice IS NULL OR EXISTS (" +
            "    SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan <= :maxPrice)) " +
            "ORDER BY (SELECT COALESCE(SUM(cdh.soLuong), 0) FROM ChiTietDonHang cdh " +
            "          WHERE cdh.chiTietSanPhamId IN (" +
            "              SELECT ct2.id FROM ChiTietSanPham ct2 WHERE ct2.sanPham = s)) DESC")
    Page<SanPham> searchProductsSortByBestSeller(
            @Param("search") String search,
            @Param("categoryIds") Set<Long> categoryIds,
            @Param("trangThai") Boolean trangThai,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            Pageable pageable
    );


    @Query(value = "SELECT s FROM SanPham s " +
            "WHERE (:search IS NULL OR :search = '' " +
            "      OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :search, '%'))) " +
            "AND (:#{#categoryIds == null} = true OR s.danhMuc.id IN :categoryIds) " +
            "AND (:trangThai IS NULL OR s.trangThai = :trangThai) " +
            "AND (:minPrice IS NULL OR EXISTS (SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan >= :minPrice)) " +
            "AND (:maxPrice IS NULL OR EXISTS (SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan <= :maxPrice)) " +
            "AND (:minRating IS NULL OR (" +
            "    EXISTS (SELECT dg FROM DanhGia dg WHERE dg.sanPhamId = s.id) " +
            "    AND (SELECT AVG(dg.diemDanhGia * 1.0) FROM DanhGia dg WHERE dg.sanPhamId = s.id) >= :minRating)) " +
            "ORDER BY s.id DESC",
            countQuery = "SELECT COUNT(s) FROM SanPham s " +
                    "WHERE (:search IS NULL OR :search = '' " +
                    "      OR LOWER(s.tenSanPham) LIKE LOWER(CONCAT('%', :search, '%'))) " +
                    "AND (:#{#categoryIds == null} = true OR s.danhMuc.id IN :categoryIds) " +
                    "AND (:trangThai IS NULL OR s.trangThai = :trangThai) " +
                    "AND (:minPrice IS NULL OR EXISTS (SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan >= :minPrice)) " +
                    "AND (:maxPrice IS NULL OR EXISTS (SELECT ct FROM ChiTietSanPham ct WHERE ct.sanPham = s AND ct.giaBan <= :maxPrice)) " +
                    "AND (:minRating IS NULL OR (" +
                    "    EXISTS (SELECT dg FROM DanhGia dg WHERE dg.sanPhamId = s.id) " +
                    "    AND (SELECT AVG(dg.diemDanhGia * 1.0) FROM DanhGia dg WHERE dg.sanPhamId = s.id) >= :minRating))")
    Page<SanPham> searchProductsWithRating(
            @Param("search") String search,
            @Param("categoryIds") Set<Long> categoryIds,
            @Param("trangThai") Boolean trangThai,
            @Param("minPrice") Double minPrice,
            @Param("maxPrice") Double maxPrice,
            @Param("minRating") Double minRating,
            Pageable pageable
    );

    // Thay thế findByDanhMucIdAndTrangThaiTrue
    @Query("SELECT s FROM SanPham s WHERE s.danhMuc.id IN :ids AND s.trangThai = true")
    List<SanPham> findByDanhMucIdInAndTrangThaiTrue(@Param("ids") Set<Long> ids);
}