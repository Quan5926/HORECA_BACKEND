package com.example.horecaone.service.voucher;

import com.example.horecaone.dto.voucher.KhuyenMaiFlashDTO;
import com.example.horecaone.entity.voucher.KhuyenMaiFlash;
import com.example.horecaone.repository.voucher.KhuyenMaiFlashRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class KhuyenMaiFlashService {

    private final KhuyenMaiFlashRepository repo;

    // ── Lấy tất cả (admin) ──────────────────────────────────
    public List<KhuyenMaiFlashDTO.Response> getAll() {
        return repo.findAll().stream()
                .map(this::toResponse)
                .collect(Collectors.toList());
    }

    // ── Lấy theo id ─────────────────────────────────────────
    public KhuyenMaiFlashDTO.Response getById(Long id) {
        KhuyenMaiFlash entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá ID: " + id));
        return toResponse(entity);
    }

    // ── Tạo mới ─────────────────────────────────────────────
    @Transactional
    public KhuyenMaiFlashDTO.Response create(KhuyenMaiFlashDTO.Request req) {
        validateRequest(req, null);

        KhuyenMaiFlash entity = KhuyenMaiFlash.builder()
                .chiTietSanPhamId(req.getChiTietSanPhamId())
                .phanTramGiamGia(req.getPhanTramGiamGia())
                .ngayBatDau(req.getNgayBatDau())
                .ngayKetThuc(req.getNgayKetThuc())
                .trangThai(req.getTrangThai() != null ? req.getTrangThai() : true)
                .nguoiTao(req.getNguoiTao())
                .build();

        return toResponse(repo.save(entity));
    }

    // ── Cập nhật ────────────────────────────────────────────
    @Transactional
    public KhuyenMaiFlashDTO.Response update(Long id, KhuyenMaiFlashDTO.Request req) {
        KhuyenMaiFlash entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá ID: " + id));

        validateRequest(req, id);

        entity.setChiTietSanPhamId(req.getChiTietSanPhamId());
        entity.setPhanTramGiamGia(req.getPhanTramGiamGia());
        entity.setNgayBatDau(req.getNgayBatDau());
        entity.setNgayKetThuc(req.getNgayKetThuc());
        if (req.getTrangThai() != null) entity.setTrangThai(req.getTrangThai());
        entity.setNguoiSua(req.getNguoiSua());

        return toResponse(repo.save(entity));
    }

    // ── Bật / Tắt nhanh ─────────────────────────────────────
    @Transactional
    public KhuyenMaiFlashDTO.Response toggleTrangThai(Long id, Long nguoiSua) {
        KhuyenMaiFlash entity = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy đợt giảm giá ID: " + id));
        entity.setTrangThai(!entity.getTrangThai());
        entity.setNguoiSua(nguoiSua);
        return toResponse(repo.save(entity));
    }

    // ── Xóa ─────────────────────────────────────────────────
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new RuntimeException("Không tìm thấy đợt giảm giá ID: " + id);
        repo.deleteById(id);
    }

    // ── Lấy flash sale đang chạy của sản phẩm (dùng ở checkout / trang SP) ──
    public KhuyenMaiFlashDTO.Response getActiveByChiTietSanPham(Long chiTietSanPhamId) {
        return repo.findActiveByChiTietSanPhamId(chiTietSanPhamId, LocalDateTime.now())
                .map(this::toResponse)
                .orElse(null);
    }

    // ── Validate ────────────────────────────────────────────
    private void validateRequest(KhuyenMaiFlashDTO.Request req, Long excludeId) {
        if (req.getPhanTramGiamGia() == null || req.getPhanTramGiamGia() < 1 || req.getPhanTramGiamGia() > 100)
            throw new IllegalArgumentException("Phần trăm giảm giá phải từ 1 đến 100");

        if (req.getNgayBatDau() == null || req.getNgayKetThuc() == null)
            throw new IllegalArgumentException("Ngày bắt đầu và kết thúc không được để trống");

        if (!req.getNgayBatDau().isBefore(req.getNgayKetThuc()))
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");

        // Kiểm tra không bị trùng thời gian với flash sale khác của cùng sản phẩm
        Long safeExcludeId = excludeId != null ? excludeId : -1L;
        boolean overlap = repo.existsOverlap(
                req.getChiTietSanPhamId(),
                req.getNgayBatDau(),
                req.getNgayKetThuc(),
                safeExcludeId
        );
        if (overlap)
            throw new IllegalArgumentException("Sản phẩm này đã có đợt giảm giá trong khoảng thời gian trùng lặp");
    }

    // ── Map entity → response ────────────────────────────────
    private KhuyenMaiFlashDTO.Response toResponse(KhuyenMaiFlash e) {
        return KhuyenMaiFlashDTO.Response.builder()
                .id(e.getId())
                .chiTietSanPhamId(e.getChiTietSanPhamId())
                .phanTramGiamGia(e.getPhanTramGiamGia())
                .ngayBatDau(e.getNgayBatDau())
                .ngayKetThuc(e.getNgayKetThuc())
                .trangThai(e.getTrangThai())
                .trangThaiHienThi(computeTrangThaiHienThi(e))
                .ngayTao(e.getNgayTao())
                .ngaySua(e.getNgaySua())
                .build();
    }

    private String computeTrangThaiHienThi(KhuyenMaiFlash e) {
        if (!e.getTrangThai()) return "Đã kết thúc";
        LocalDateTime now = LocalDateTime.now();
        if (e.getNgayBatDau().isAfter(now)) return "Sắp tới";
        if (e.getNgayKetThuc().isBefore(now)) return "Đã kết thúc";
        return "Đang diễn ra";
    }
}