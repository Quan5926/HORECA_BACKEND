package com.example.horecaone.service.voucher;

import com.example.horecaone.dto.voucher.MaGiamGiaDTO;
import com.example.horecaone.entity.voucher.MaGiamGia;
import com.example.horecaone.repository.voucher.MaGiamGiaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MaGiamGiaService {

    private final MaGiamGiaRepository repo;

    public List<MaGiamGiaDTO.Response> getAll() {
        return repo.findAll().stream().map(this::toResponse).collect(Collectors.toList());
    }

    public MaGiamGiaDTO.Response getById(Long id) {
        return toResponse(findOrThrow(id));
    }

    public List<MaGiamGiaDTO.Response> getAllValid() {
        return repo.findAllValid(LocalDateTime.now()).stream().map(this::toResponse).collect(Collectors.toList());
    }

    // ── Tạo mới ─────────────────────────────────────────────
    @Transactional
    public MaGiamGiaDTO.Response create(MaGiamGiaDTO.Request req) {
        validateRequest(req, null);

        MaGiamGia entity = MaGiamGia.builder()
                .maVoucher(req.getMaVoucher().toUpperCase())
                .loaiGiam(req.getLoaiGiam())
                .giaTriGiam(req.getGiaTriGiam())
                .donToiThieu(req.getDonToiThieu())
                .giamToiDa(req.getGiamToiDa())
                .gioiHanSuDung(req.getGioiHanSuDung())
                .daSuDung(0)
                .ngayBatDau(req.getNgayBatDau())
                .ngayKetThuc(req.getNgayKetThuc())
                .trangThaiHoatDong(req.getTrangThaiHoatDong() != null ? req.getTrangThaiHoatDong() : true)
                // field mới
                .moTa(req.getMoTa())
                .phamViApDung(req.getPhamViApDung() != null ? req.getPhamViApDung() : "ALL")
                .doiTuong(req.getDoiTuong()       != null ? req.getDoiTuong()       : "ALL")
                .moiKhach1Lan(req.getMoiKhach1Lan() != null ? req.getMoiKhach1Lan() : true)
                .nguoiTao(req.getNguoiTao())
                .build();

        return toResponse(repo.save(entity));
    }

    // ── Cập nhật ────────────────────────────────────────────
    @Transactional
    public MaGiamGiaDTO.Response update(Long id, MaGiamGiaDTO.Request req) {
        MaGiamGia entity = findOrThrow(id);
        validateRequest(req, id);

        entity.setMaVoucher(req.getMaVoucher().toUpperCase());
        entity.setLoaiGiam(req.getLoaiGiam());
        entity.setGiaTriGiam(req.getGiaTriGiam());
        entity.setDonToiThieu(req.getDonToiThieu());
        entity.setGiamToiDa(req.getGiamToiDa());
        entity.setGioiHanSuDung(req.getGioiHanSuDung());
        entity.setNgayBatDau(req.getNgayBatDau());
        entity.setNgayKetThuc(req.getNgayKetThuc());
        if (req.getTrangThaiHoatDong() != null) entity.setTrangThaiHoatDong(req.getTrangThaiHoatDong());
        // field mới
        entity.setMoTa(req.getMoTa());
        if (req.getPhamViApDung() != null) entity.setPhamViApDung(req.getPhamViApDung());
        if (req.getDoiTuong()     != null) entity.setDoiTuong(req.getDoiTuong());
        if (req.getMoiKhach1Lan() != null) entity.setMoiKhach1Lan(req.getMoiKhach1Lan());
        entity.setNguoiSua(req.getNguoiSua());

        return toResponse(repo.save(entity));
    }

    // ── Toggle ───────────────────────────────────────────────
    @Transactional
    public MaGiamGiaDTO.Response toggleTrangThai(Long id, Long nguoiSua) {
        MaGiamGia entity = findOrThrow(id);
        entity.setTrangThaiHoatDong(!entity.getTrangThaiHoatDong());
        entity.setNguoiSua(nguoiSua);
        return toResponse(repo.save(entity));
    }

    // ── Xóa ─────────────────────────────────────────────────
    @Transactional
    public void delete(Long id) {
        if (!repo.existsById(id))
            throw new RuntimeException("Không tìm thấy mã giảm giá ID: " + id);
        repo.deleteById(id);
    }

    // ── Apply mã tại checkout ────────────────────────────────
    public MaGiamGiaDTO.ApplyResponse apply(MaGiamGiaDTO.ApplyRequest req) {
        MaGiamGia entity = repo.findByMaVoucherIgnoreCase(req.getMaVoucher())
                .orElseThrow(() -> new IllegalArgumentException("Mã giảm giá không tồn tại: " + req.getMaVoucher()));

        if (!entity.getTrangThaiHoatDong())
            throw new IllegalArgumentException("Mã giảm giá không còn hoạt động");

        LocalDateTime now = LocalDateTime.now();
        if (entity.getNgayBatDau() != null && now.isBefore(entity.getNgayBatDau()))
            throw new IllegalArgumentException("Mã giảm giá chưa đến ngày áp dụng");
        if (entity.getNgayKetThuc() != null && now.isAfter(entity.getNgayKetThuc()))
            throw new IllegalArgumentException("Mã giảm giá đã hết hạn");
        if (entity.getGioiHanSuDung() != null && entity.getDaSuDung() >= entity.getGioiHanSuDung())
            throw new IllegalArgumentException("Mã giảm giá đã hết lượt sử dụng");
        if (req.getTongDonHang().compareTo(entity.getDonToiThieu()) < 0)
            throw new IllegalArgumentException("Đơn hàng tối thiểu " + entity.getDonToiThieu() + "đ để dùng mã này");

        return MaGiamGiaDTO.ApplyResponse.builder()
                .maGiamGiaId(entity.getId())
                .maVoucher(entity.getMaVoucher())
                .loaiGiam(entity.getLoaiGiam())
                .soTienGiam(computeSoTienGiam(entity, req.getTongDonHang()))
                .thongBao("Áp dụng thành công")
                .build();
    }

    @Transactional
    public void confirmApply(Long maGiamGiaId) {
        repo.incrementDaSuDung(maGiamGiaId);
    }

    // ── Helpers ──────────────────────────────────────────────
    private BigDecimal computeSoTienGiam(MaGiamGia e, BigDecimal tongDon) {
        return switch (e.getLoaiGiam()) {
            case "PHAN_TRAM" -> {
                BigDecimal giam = tongDon.multiply(e.getGiaTriGiam()).divide(BigDecimal.valueOf(100));
                if (e.getGiamToiDa() != null) giam = giam.min(e.getGiamToiDa());
                yield giam;
            }
            case "CO_DINH"  -> e.getGiaTriGiam().min(tongDon);
            case "FREESHIP" -> BigDecimal.ZERO;
            default -> throw new IllegalArgumentException("Loại giảm không hợp lệ: " + e.getLoaiGiam());
        };
    }

    private void validateRequest(MaGiamGiaDTO.Request req, Long excludeId) {
        if (req.getMaVoucher() == null || req.getMaVoucher().isBlank())
            throw new IllegalArgumentException("Mã voucher không được để trống");

        boolean exists = repo.existsByMaVoucherIgnoreCase(req.getMaVoucher());
        if (exists) {
            if (excludeId == null)
                throw new IllegalArgumentException("Mã voucher đã tồn tại: " + req.getMaVoucher());
            repo.findByMaVoucherIgnoreCase(req.getMaVoucher()).ifPresent(existing -> {
                if (!existing.getId().equals(excludeId))
                    throw new IllegalArgumentException("Mã voucher đã tồn tại: " + req.getMaVoucher());
            });
        }

        List<String> validLoai = List.of("PHAN_TRAM", "CO_DINH", "FREESHIP");
        if (!validLoai.contains(req.getLoaiGiam()))
            throw new IllegalArgumentException("Loại giảm không hợp lệ. Chỉ chấp nhận: " + validLoai);

        if (req.getGiaTriGiam() == null || req.getGiaTriGiam().compareTo(BigDecimal.ZERO) < 0)
            throw new IllegalArgumentException("Giá trị giảm phải >= 0");

        if ("PHAN_TRAM".equals(req.getLoaiGiam()) && req.getGiaTriGiam().compareTo(BigDecimal.valueOf(100)) > 0)
            throw new IllegalArgumentException("Phần trăm giảm không được vượt quá 100");

        if (req.getNgayBatDau() != null && req.getNgayKetThuc() != null
                && !req.getNgayBatDau().isBefore(req.getNgayKetThuc()))
            throw new IllegalArgumentException("Ngày bắt đầu phải trước ngày kết thúc");

        List<String> validPhamVi  = List.of("ALL", "CATEGORY", "SPECIFIC");
        List<String> validDoiTuong = List.of("ALL", "NEW", "VIP");
        if (req.getPhamViApDung() != null && !validPhamVi.contains(req.getPhamViApDung()))
            throw new IllegalArgumentException("Phạm vi áp dụng không hợp lệ: " + validPhamVi);
        if (req.getDoiTuong() != null && !validDoiTuong.contains(req.getDoiTuong()))
            throw new IllegalArgumentException("Đối tượng không hợp lệ: " + validDoiTuong);
    }

    private String computeTrangThaiHienThi(MaGiamGia e) {
        if (!e.getTrangThaiHoatDong()) return "Tạm dừng";
        if (e.getGioiHanSuDung() != null && e.getDaSuDung() >= e.getGioiHanSuDung()) return "Hết lượt";
        LocalDateTime now = LocalDateTime.now();
        if (e.getNgayBatDau() != null && now.isBefore(e.getNgayBatDau())) return "Sắp tới";
        if (e.getNgayKetThuc() != null && now.isAfter(e.getNgayKetThuc())) return "Đã hết hạn";
        return "Đang diễn ra";
    }

    private MaGiamGiaDTO.Response toResponse(MaGiamGia e) {
        return MaGiamGiaDTO.Response.builder()
                .id(e.getId())
                .maVoucher(e.getMaVoucher())
                .loaiGiam(e.getLoaiGiam())
                .giaTriGiam(e.getGiaTriGiam())
                .donToiThieu(e.getDonToiThieu())
                .giamToiDa(e.getGiamToiDa())
                .gioiHanSuDung(e.getGioiHanSuDung())
                .daSuDung(e.getDaSuDung())
                .ngayBatDau(e.getNgayBatDau())
                .ngayKetThuc(e.getNgayKetThuc())
                .trangThaiHoatDong(e.getTrangThaiHoatDong())
                .trangThaiHienThi(computeTrangThaiHienThi(e))
                // field mới
                .moTa(e.getMoTa())
                .phamViApDung(e.getPhamViApDung())
                .doiTuong(e.getDoiTuong())
                .moiKhach1Lan(e.getMoiKhach1Lan())
                .ngayTao(e.getNgayTao())
                .ngaySua(e.getNgaySua())
                .build();
    }

    private MaGiamGia findOrThrow(Long id) {
        return repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy mã giảm giá ID: " + id));
    }
}