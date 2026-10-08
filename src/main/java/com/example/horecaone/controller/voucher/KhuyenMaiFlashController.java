package com.example.horecaone.controller.voucher;

import com.example.horecaone.dto.voucher.KhuyenMaiFlashDTO;
import com.example.horecaone.service.voucher.KhuyenMaiFlashService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/khuyen-mai-flash")
@RequiredArgsConstructor
public class KhuyenMaiFlashController {

    private final KhuyenMaiFlashService service;

    // ── GET /api/khuyen-mai-flash
    //    Admin lấy toàn bộ danh sách
    @GetMapping
    public ResponseEntity<List<KhuyenMaiFlashDTO.Response>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ── GET /api/khuyen-mai-flash/{id}
    //    Lấy chi tiết 1 đợt
    @GetMapping("/{id}")
    public ResponseEntity<KhuyenMaiFlashDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ── GET /api/khuyen-mai-flash/san-pham/{chiTietSanPhamId}/active
    //    Lấy flash sale đang chạy của 1 sản phẩm (dùng ở trang SP / checkout)
    @GetMapping("/san-pham/{chiTietSanPhamId}/active")
    public ResponseEntity<KhuyenMaiFlashDTO.Response> getActiveBySanPham(
            @PathVariable Long chiTietSanPhamId) {
        KhuyenMaiFlashDTO.Response result = service.getActiveByChiTietSanPham(chiTietSanPhamId);
        if (result == null) return ResponseEntity.noContent().build();
        return ResponseEntity.ok(result);
    }

    // ── POST /api/khuyen-mai-flash
    //    Tạo đợt giảm giá mới
    @PostMapping
    public ResponseEntity<KhuyenMaiFlashDTO.Response> create(
            @Valid @RequestBody KhuyenMaiFlashDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    // ── PUT /api/khuyen-mai-flash/{id}
    //    Cập nhật đợt giảm giá
    @PutMapping("/{id}")
    public ResponseEntity<KhuyenMaiFlashDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody KhuyenMaiFlashDTO.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // ── PATCH /api/khuyen-mai-flash/{id}/toggle?nguoiSua=1
    //    Bật / Tắt nhanh trạng thái
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<KhuyenMaiFlashDTO.Response> toggle(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Long nguoiSua) {
        return ResponseEntity.ok(service.toggleTrangThai(id, nguoiSua));
    }

    // ── DELETE /api/khuyen-mai-flash/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}