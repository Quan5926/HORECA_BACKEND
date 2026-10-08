package com.example.horecaone.controller.voucher;

import com.example.horecaone.dto.voucher.MaGiamGiaDTO;
import com.example.horecaone.service.voucher.MaGiamGiaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ma-giam-gia")
@RequiredArgsConstructor
public class MaGiamGiaController {

    private final MaGiamGiaService service;

    // ── GET /api/ma-giam-gia
    //    Admin lấy toàn bộ (tất cả trạng thái)
    @GetMapping
    public ResponseEntity<List<MaGiamGiaDTO.Response>> getAll() {
        return ResponseEntity.ok(service.getAll());
    }

    // ── GET /api/ma-giam-gia/{id}
    @GetMapping("/{id}")
    public ResponseEntity<MaGiamGiaDTO.Response> getById(@PathVariable Long id) {
        return ResponseEntity.ok(service.getById(id));
    }

    // ── GET /api/ma-giam-gia/valid
    //    User xem danh sách voucher còn hiệu lực (dropdown checkout)
    @GetMapping("/valid")
    public ResponseEntity<List<MaGiamGiaDTO.Response>> getValid() {
        return ResponseEntity.ok(service.getAllValid());
    }

    // ── POST /api/ma-giam-gia/apply
    //    Khách nhập mã → validate + trả về số tiền giảm
    //    Dùng POST vì body có tongDonHang (không nên để query param)
    @PostMapping("/apply")
    public ResponseEntity<MaGiamGiaDTO.ApplyResponse> apply(
            @RequestBody MaGiamGiaDTO.ApplyRequest req) {
        return ResponseEntity.ok(service.apply(req));
    }

    // ── POST /api/ma-giam-gia/{id}/confirm-apply
    //    Gọi sau khi đơn hàng lưu DB thành công để tăng daSuDung
    @PostMapping("/{id}/confirm-apply")
    public ResponseEntity<Void> confirmApply(@PathVariable Long id) {
        service.confirmApply(id);
        return ResponseEntity.noContent().build();
    }

    // ── POST /api/ma-giam-gia
    //    Admin tạo voucher mới
    @PostMapping
    public ResponseEntity<MaGiamGiaDTO.Response> create(
            @Valid @RequestBody MaGiamGiaDTO.Request req) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(req));
    }

    // ── PUT /api/ma-giam-gia/{id}
    @PutMapping("/{id}")
    public ResponseEntity<MaGiamGiaDTO.Response> update(
            @PathVariable Long id,
            @Valid @RequestBody MaGiamGiaDTO.Request req) {
        return ResponseEntity.ok(service.update(id, req));
    }

    // ── PATCH /api/ma-giam-gia/{id}/toggle?nguoiSua=1
    //    Bật / Tắt nhanh
    @PatchMapping("/{id}/toggle")
    public ResponseEntity<MaGiamGiaDTO.Response> toggle(
            @PathVariable Long id,
            @RequestParam(defaultValue = "0") Long nguoiSua) {
        return ResponseEntity.ok(service.toggleTrangThai(id, nguoiSua));
    }

    // ── DELETE /api/ma-giam-gia/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}