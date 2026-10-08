package com.example.horecaone.controller.product;

import com.example.horecaone.dto.product.*;
import com.example.horecaone.service.product.SanPhamService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/products")

@RequiredArgsConstructor
public class SanPhamController {

    private final SanPhamService sanPhamService;

    // ===================================================================================
    // 1. GET
    // ===================================================================================

    @GetMapping("/category/{categoryId}")
    public ResponseEntity<List<SanPhamResponseDTO>> getProductsByCategory(
            @PathVariable Long categoryId) {
        return ResponseEntity.ok(sanPhamService.getProductsByCategoryId(categoryId));
    }

    @GetMapping
    public ResponseEntity<Page<SanPhamResponseDTO>> getAllProducts(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false, defaultValue = "true") Boolean trangThai,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        Sort sort = switch (sortBy) {
            case "price_asc"  -> Sort.by(Sort.Direction.ASC,  "id");
            case "price_desc" -> Sort.by(Sort.Direction.DESC, "id");
            default           -> Sort.by(Sort.Direction.DESC, "id");
        };

        Pageable pageable = PageRequest.of(page - 1, size, sort);
        return ResponseEntity.ok(sanPhamService.getProducts(
                search, categoryId, trangThai, minPrice, maxPrice, sortBy, minRating, pageable));
    }

    // ⚠️ /search phải đặt TRƯỚC /{id}
    @GetMapping("/search")
    public ResponseEntity<Page<SanPhamResponseDTO>> searchProducts(
            @RequestParam(required = false, defaultValue = "") String keyword,
            @RequestParam(defaultValue = "6") int size) {
        Pageable pageable = PageRequest.of(0, size);
        return ResponseEntity.ok(sanPhamService.searchByKeyword(keyword, pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<SanPhamDetailDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(sanPhamService.getProductById(id));
    }

    @GetMapping("/by-chi-tiet/{chiTietSanPhamId}")
    public ResponseEntity<SanPhamDetailDTO> getProductByChiTietId(
            @PathVariable Long chiTietSanPhamId) {
        return ResponseEntity.ok(sanPhamService.getProductByChiTietId(chiTietSanPhamId));
    }

    @GetMapping("/{id}/related")
    public ResponseEntity<List<SanPhamResponseDTO>> getRelatedProducts(
            @PathVariable("id") Long currentProductId,
            @RequestParam("categoryId") Long categoryId) {
        return ResponseEntity.ok(sanPhamService.getRelatedProducts(categoryId, currentProductId));
    }

    // ===================================================================================
    // 2. POST / PUT / DELETE
    // ===================================================================================

    @PostMapping
    public ResponseEntity<SanPhamDetailDTO> createProduct(@RequestBody SanPhamRequestDTO req) {
        return ResponseEntity.ok(sanPhamService.saveProduct(null, req));
    }

    @PutMapping("/{id}")
    public ResponseEntity<SanPhamDetailDTO> updateProduct(
            @PathVariable Long id, @RequestBody SanPhamRequestDTO req) {
        return ResponseEntity.ok(sanPhamService.saveProduct(id, req));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        sanPhamService.deleteProduct(id);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteBulkProducts(@RequestBody List<Long> ids) {
        sanPhamService.deleteBulkProducts(ids);
        return ResponseEntity.ok().build();
    }

    // ===================================================================================
    // 3. UPLOAD ẢNH
    // ===================================================================================

    // 3.1 Upload nhiều ảnh cho sản phẩm
    @PostMapping("/{id}/upload-images")
    public ResponseEntity<SanPhamDetailDTO> uploadImages(
            @PathVariable Long id,
            @RequestParam("files") List<MultipartFile> files) {
        return ResponseEntity.ok(sanPhamService.uploadImages(id, files));
    }

    // 3.2 Upload ảnh cho 1 biến thể (MỚI)
    // FE gọi: POST /api/products/variants/{variantId}/upload-image
    @PostMapping("/variants/{variantId}/upload-image")
    public ResponseEntity<ChiTietSanPhamDTO> uploadVariantImage(
            @PathVariable Long variantId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(sanPhamService.uploadVariantImage(variantId, file));
    }

    // ===================================================================================
    // 4. EXCEPTION HANDLER
    // ===================================================================================

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<Object> handleDuplicateKey(DataIntegrityViolationException e) {
        if (e.getMessage() != null && e.getMessage().contains("UNIQUE KEY constraint")) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(java.util.Map.of("message",
                            "Mã SKU đã tồn tại trong hệ thống. Vui lòng nhập mã khác!"));
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(java.util.Map.of("message", "Lỗi dữ liệu: " + e.getMessage()));
    }

    // Upload ảnh cho combo item
    @PostMapping("/combo-items/{comboItemId}/upload-image")
    public ResponseEntity<ChiTietComboDTO> uploadComboItemImage(
            @PathVariable Long comboItemId,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(sanPhamService.uploadComboItemImage(comboItemId, file));
    }


    @GetMapping("/admin/list")
    public ResponseEntity<Page<SanPhamResponseDTO>> getAllProductsAdmin(
            @RequestParam(required = false, defaultValue = "") String search,
            @RequestParam(required = false) Long categoryId,
            @RequestParam(required = false) Boolean trangThai,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false, defaultValue = "newest") String sortBy,
            @RequestParam(required = false) Double minRating,
            @RequestParam(defaultValue = "1") int page,
            @RequestParam(defaultValue = "15") int size) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by(Sort.Direction.DESC, "id"));
        return ResponseEntity.ok(sanPhamService.getProducts(
                search, categoryId, trangThai, minPrice, maxPrice, sortBy, minRating, pageable));
    }
}
