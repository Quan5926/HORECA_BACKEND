package com.example.horecaone.service.product;

import com.example.horecaone.dto.product.ChiTietComboDTO;
import com.example.horecaone.dto.product.ChiTietSanPhamDTO;
import com.example.horecaone.dto.product.SanPhamDetailDTO;
import com.example.horecaone.dto.product.SanPhamRequestDTO;
import com.example.horecaone.dto.product.SanPhamResponseDTO;
import com.example.horecaone.entity.categories.DanhMuc;
import com.example.horecaone.entity.product.AnhSanPham;
import com.example.horecaone.entity.product.ChiTietCombo;
import com.example.horecaone.entity.product.ChiTietSanPham;
import com.example.horecaone.entity.product.SanPham;
import com.example.horecaone.repository.cart.ChiTietDonHangRepository;
import com.example.horecaone.repository.categories.DanhMucRepository;
import com.example.horecaone.repository.product.ChiTietComboRepository;
import com.example.horecaone.repository.product.ChiTietSanPhamRepository;
import com.example.horecaone.repository.product.SanPhamRepository;
import com.example.horecaone.repository.reviews.DanhGiaRepository;
import com.example.horecaone.service.banner.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.example.horecaone.repository.categories.DanhMucRepository;
import java.util.Set;
import java.util.HashSet;
import java.util.Queue;
import java.util.LinkedList;
@Service
@RequiredArgsConstructor
public class SanPhamService {

    private final SanPhamRepository        sanPhamRepository;
    private final DanhMucRepository        danhMucRepository;
    private final ChiTietSanPhamRepository chiTietSanPhamRepository;
    private final ChiTietComboRepository   chiTietComboRepository;
    private final FileService              fileService;
    private final DanhGiaRepository        danhGiaRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;

    // ===================================================================================
    // PHẦN 1: ĐỌC DỮ LIỆU (READ)
    // ===================================================================================

    public List<SanPhamResponseDTO> getProductsByCategoryId(Long categoryId) {
        Set<Long> allIds = getAllDescendantIds(categoryId);
        return sanPhamRepository.findByDanhMucIdInAndTrangThaiTrue(allIds)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public Page<SanPhamResponseDTO> getProducts(
            String search, Long categoryId, Boolean trangThai,
            Double minPrice, Double maxPrice,
            String sortBy, Double minRating, Pageable pageable) {

        // Expand categoryId → tập id bao gồm cả con/cháu
        Set<Long> categoryIds = categoryId != null ? getAllDescendantIds(categoryId) : null;

        Pageable unsorted = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize());
        Pageable newestP  = PageRequest.of(pageable.getPageNumber(), pageable.getPageSize(),
                Sort.by(Sort.Direction.DESC, "id"));

        Page<SanPham> sanPhams;

        if (minRating != null) {
            sanPhams = sanPhamRepository.searchProductsWithRating(
                    search, categoryIds, trangThai, minPrice, maxPrice, minRating, unsorted);
        } else {
            switch (sortBy != null ? sortBy : "newest") {
                case "price_asc":
                    sanPhams = sanPhamRepository.searchProductsSortByPriceAsc(
                            search, categoryIds, trangThai, minPrice, maxPrice, unsorted);
                    break;
                case "price_desc":
                    sanPhams = sanPhamRepository.searchProductsSortByPriceDesc(
                            search, categoryIds, trangThai, minPrice, maxPrice, unsorted);
                    break;
                case "bestseller":
                    sanPhams = sanPhamRepository.searchProductsSortByBestSeller(
                            search, categoryIds, trangThai, minPrice, maxPrice, unsorted);
                    break;
                default:
                    sanPhams = sanPhamRepository.searchProducts(
                            search, categoryIds, trangThai, minPrice, maxPrice, newestP);
            }
        }

        // ── Batch query stats thay vì N+1 ──
        List<Long> ids = sanPhams.getContent()
                .stream().map(SanPham::getId).collect(Collectors.toList());

        Map<Long, Integer> daBanMap;
        Map<Long, Double>  ratingMap;

        if (!ids.isEmpty()) {
            daBanMap = chiTietDonHangRepository
                    .countSoldGroupByProductIds(ids)
                    .stream().collect(Collectors.toMap(
                            arr -> (Long)   arr[0],
                            arr -> ((Number) arr[1]).intValue()
                    ));

            ratingMap = danhGiaRepository
                    .getAverageRatingsGroupByProductIds(ids)
                    .stream().collect(Collectors.toMap(
                            arr -> (Long) arr[0],
                            arr -> arr[1] != null
                                    ? Math.round(((Double) arr[1]) * 10.0) / 10.0
                                    : 0.0
                    ));
        } else {
            daBanMap  = new java.util.HashMap<>();
            ratingMap = new java.util.HashMap<>();
        }

        return sanPhams.map(sp -> mapToResponseDTOWithStats(sp, daBanMap, ratingMap));
    }

    private SanPhamResponseDTO mapToResponseDTOWithStats(
            SanPham entity,
            Map<Long, Integer> daBanMap,
            Map<Long, Double> ratingMap) {

        SanPhamResponseDTO dto = mapToResponseDTO(entity); // reuse logic cũ
        dto.setDaBan(daBanMap.getOrDefault(entity.getId(), 0));
        dto.setDiemDanhGia(ratingMap.getOrDefault(entity.getId(), 0.0));
        return dto;
    }


    public SanPhamDetailDTO getProductById(Long id) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm id: " + id));

        SanPhamDetailDTO dto = mapToDetailDTO(sanPham);

        Integer daBan = chiTietDonHangRepository.countTotalSoldBySanPhamId(id);
        dto.setDaBan(daBan != null ? daBan : 0);

        Double saoTB = danhGiaRepository.getAverageStarsByProductId(id);
        dto.setDiemDanhGia(saoTB != null ? Math.round(saoTB * 10.0) / 10.0 : 0.0);

        Long tongDG = danhGiaRepository.countTotalReviewsByProductId(id);
        dto.setSoLuotDanhGia(tongDG != null ? tongDG : 0L);

        dto.setReviews(danhGiaRepository.findApprovedReviewsByProductId(id));

        return dto;
    }

    public List<SanPhamResponseDTO> getRelatedProducts(Long categoryId, Long currentProductId) {
        Pageable limit = PageRequest.of(0, 5);
        return sanPhamRepository.findRelatedProducts(categoryId, currentProductId, limit)
                .stream().map(this::mapToResponseDTO).collect(Collectors.toList());
    }

    public Page<SanPhamResponseDTO> searchByKeyword(String keyword, Pageable pageable) {
        return sanPhamRepository.searchByKeyword(keyword, pageable).map(this::mapToResponseDTO);
    }

    // ===================================================================================
    // PHẦN 2: GHI DỮ LIỆU (WRITE)
    // ===================================================================================

    @Transactional
    public SanPhamDetailDTO saveProduct(Long id, SanPhamRequestDTO req) {
        SanPham sanPham;

        if (id != null) {
            sanPham = sanPhamRepository.findById(id)
                    .orElseThrow(() -> new RuntimeException("Sản phẩm không tồn tại"));
        } else {
            sanPham = new SanPham();
            sanPham.setChiTietSanPhams(new ArrayList<>());
            sanPham.setAnhSanPhams(new ArrayList<>());
        }

        // ── Map các field SAN_PHAM ──
        sanPham.setTenSanPham(req.getTenSanPham());
        sanPham.setMoTaNgan(req.getMoTaNgan());
        sanPham.setMoTaChiTiet(req.getMoTaChiTiet());
        sanPham.setTrangThai(req.getTrangThai());
        sanPham.setLaCombo(req.getLaCombo() != null ? req.getLaCombo() : false);

        // ── Map field bổ sung ──
        sanPham.setThuongHieu(req.getThuongHieu());
        sanPham.setChatLieu(req.getChatLieu());
        sanPham.setKieuDang(req.getKieuDang());
        sanPham.setChatLuong(req.getChatLuong());
        sanPham.setXuatXu(req.getXuatXu());
        sanPham.setTronBo(req.getTronBo());
        sanPham.setPhuHopCho(req.getPhuHopCho());

        // ── Liên hệ ──
        sanPham.setLienHe(req.getLienHe() != null ? req.getLienHe() : false);
        sanPham.setSoDienThoai(req.getSoDienThoai());
        sanPham.setZaloLink(req.getZaloLink());

        // ── Danh mục ──
        if (req.getDanhMucId() != null) {
            DanhMuc danhMuc = danhMucRepository.findById(req.getDanhMucId())
                    .orElseThrow(() -> new RuntimeException("Danh mục không tồn tại"));
            sanPham.setDanhMuc(danhMuc);
        }

        // ── Xử lý ảnh cũ (khi update) ──
        if (id != null) {
            List<String> anhGiuLai = req.getDanhSachAnhCu();
            if (anhGiuLai != null && !anhGiuLai.isEmpty()) {
                sanPham.getAnhSanPhams().removeIf(
                        anh -> !anhGiuLai.contains(anh.getDuongDanAnh()));
            } else {
                if (sanPham.getAnhSanPhams() != null) sanPham.getAnhSanPhams().clear();
            }
        }

        // ── Xử lý biến thể ──
        if (req.getChiTietSanPhams() != null && !req.getChiTietSanPhams().isEmpty()) {
            saveVariants(sanPham, req.getChiTietSanPhams());
        } else {
            saveSingleVariant(sanPham, req);
        }

        // ── Lưu + flush để biến thể có ID trước khi xử lý combo ──
        SanPham saved = sanPhamRepository.saveAndFlush(sanPham);

        // ── Reload từ DB để lấy chiTietSanPhams với ID đầy đủ ──
        SanPham freshSanPham = sanPhamRepository.findById(saved.getId())
                .orElseThrow(() -> new RuntimeException("Lỗi reload sản phẩm"));

        // ── Xử lý combo items ──
        saveComboItems(freshSanPham, req);

        // ── Flush để combo items có ID, reload lại để response có chiTietCombos đầy đủ ──
        chiTietComboRepository.flush();

        SanPham finalSanPham = sanPhamRepository.findById(freshSanPham.getId())
                .orElseThrow(() -> new RuntimeException("Lỗi reload sản phẩm sau combo"));

        return mapToDetailDTO(finalSanPham);
    }
    /**
     * Lưu danh sách biến thể (màu + size).
     * - Biến thể có id → update
     * - Biến thể không có id → tạo mới
     * - Biến thể cũ không có trong list gửi lên → xóa
     */
    private void saveVariants(SanPham sanPham, List<ChiTietSanPhamDTO> dtoList) {
        Map<Long, ChiTietSanPham> existingMap = sanPham.getChiTietSanPhams().stream()
                .filter(v -> v.getId() != null)
                .collect(Collectors.toMap(ChiTietSanPham::getId, v -> v));

        List<Long> incomingIds = dtoList.stream()
                .filter(d -> d.getId() != null)
                .map(ChiTietSanPhamDTO::getId)
                .collect(Collectors.toList());

        List<Long> toDeleteIds = existingMap.keySet().stream()
                .filter(id -> !incomingIds.contains(id))
                .collect(Collectors.toList());

        // Xóa combo rows trỏ vào biến thể bị xóa trước
        if (!toDeleteIds.isEmpty()) {
            chiTietComboRepository.deleteByChiTietComboIdIn(toDeleteIds);
            chiTietComboRepository.flush();
        }

        List<ChiTietSanPham> updatedList = new ArrayList<>();

        for (ChiTietSanPhamDTO dto : dtoList) {
            ChiTietSanPham entity;
            if (dto.getId() != null && existingMap.containsKey(dto.getId())) {
                entity = existingMap.get(dto.getId());
            } else {
                entity = new ChiTietSanPham();
                entity.setSanPham(sanPham);
            }
            entity.setMaSku(dto.getMaSku());
            entity.setMauSac(dto.getMauSac());
            entity.setMauSacHex(dto.getMauSacHex());
            entity.setKichThuoc(dto.getKichThuoc());
            entity.setTrongLuong(dto.getTrongLuong());
            entity.setGiaNhap(dto.getGiaNhap());
            entity.setGiaBan(dto.getGiaBan());
            entity.setTonKho(dto.getTonKho() != null ? dto.getTonKho() : 0);
            entity.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
            if (dto.getAnhSanPham() != null && !dto.getAnhSanPham().isBlank()) {
                entity.setAnhSanPham(dto.getAnhSanPham());
            }
            updatedList.add(entity);
        }

        sanPham.getChiTietSanPhams().clear();
        sanPham.getChiTietSanPhams().addAll(updatedList);
    }

    /**
     * Lưu 1 biến thể đơn (khi SP không có màu/size).
     */
    private void saveSingleVariant(SanPham sanPham, SanPhamRequestDTO req) {
        ChiTietSanPham chiTiet;

        if (!sanPham.getChiTietSanPhams().isEmpty()) {
            chiTiet = sanPham.getChiTietSanPhams().get(0);
        } else {
            chiTiet = new ChiTietSanPham();
            chiTiet.setSanPham(sanPham);
            sanPham.getChiTietSanPhams().add(chiTiet);
        }

        chiTiet.setMaSku(req.getMaSku());
        chiTiet.setTrongLuong(req.getTrongLuong());
        chiTiet.setGiaNhap(req.getGiaNhap());
        chiTiet.setGiaBan(req.getGiaBan());
        chiTiet.setTonKho(req.getTonKho() != null ? req.getTonKho() : 0);
        chiTiet.setTrangThai(req.getTrangThai() != null ? req.getTrangThai() : true);
        chiTiet.setMauSac(req.getMauSac());         // ← THÊM
        chiTiet.setKichThuoc(req.getKichThuoc());   // ← THÊM
        chiTiet.setMauSacHex(req.getMauSacHex());   // ← THÊM
        // anhSanPham sẽ được set sau khi upload ảnh sản phẩm chính
    }

    /**
     * Lưu danh sách sản phẩm con trong combo.
     * - Nếu laCombo = true  → xóa combo cũ, tạo mới từ request
     * - Nếu laCombo = false → xóa toàn bộ combo cũ
     */
    private void saveComboItems(SanPham sanPham, SanPhamRequestDTO req) {
        boolean laCombo = Boolean.TRUE.equals(req.getLaCombo());

        if (sanPham.getChiTietSanPhams() == null || sanPham.getChiTietSanPhams().isEmpty()) return;

        ChiTietSanPham chiTietCha = sanPham.getChiTietSanPhams().get(0);
        if (chiTietCha.getId() == null) return;

        // Lấy danh sách combo cũ
        List<ChiTietCombo> oldCombos = chiTietComboRepository.findByChiTietComboId(chiTietCha.getId());

        if (!laCombo || req.getChiTietCombos() == null || req.getChiTietCombos().isEmpty()) {
            chiTietComboRepository.deleteByChiTietComboId(chiTietCha.getId());
            return;
        }

        // Map id cũ → entity để tra cứu ảnh cũ
        Map<Long, ChiTietCombo> oldMap = oldCombos.stream()
                .filter(c -> c.getId() != null)
                .collect(Collectors.toMap(ChiTietCombo::getId, c -> c));

        // Xóa toàn bộ combo cũ trước
        chiTietComboRepository.deleteByChiTietComboId(chiTietCha.getId());
        chiTietComboRepository.flush();

        List<ChiTietCombo> comboList = new ArrayList<>();
        for (ChiTietComboDTO comboDto : req.getChiTietCombos()) {
            ChiTietCombo combo = new ChiTietCombo();
            combo.setChiTietCombo(chiTietCha);
            combo.setTenComboItem(comboDto.getTenSanPham());
            combo.setSoLuong(comboDto.getSoLuong() != null ? comboDto.getSoLuong() : 1);
            combo.setTrangThai(true);

            // Giữ lại ảnh cũ nếu combo này có id và ảnh cũ tồn tại
            if (comboDto.getId() != null && oldMap.containsKey(comboDto.getId())) {
                combo.setAnhComboItem(oldMap.get(comboDto.getId()).getAnhComboItem());
            }

            comboList.add(combo);
        }
        chiTietComboRepository.saveAll(comboList);
    }



    // ===================================================================================
    // PHẦN 3: UPLOAD ẢNH
    // ===================================================================================

    @Transactional
    public SanPhamDetailDTO uploadImages(Long id, List<MultipartFile> files) {
        SanPham sanPham = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm id: " + id));

        if (sanPham.getAnhSanPhams() == null) sanPham.setAnhSanPhams(new ArrayList<>());

        if (files != null && !files.isEmpty()) {
            boolean isFirst = sanPham.getAnhSanPhams().isEmpty();
            int maxThuTu = sanPham.getAnhSanPhams().stream()
                    .mapToInt(a -> a.getThuTu() != null ? a.getThuTu() : 0).max().orElse(0);

            for (MultipartFile file : files) {
                String url = fileService.storeFile(file, "products");
                AnhSanPham anh = new AnhSanPham();
                anh.setDuongDanAnh(url);
                anh.setSanPham(sanPham);
                anh.setThuTu(++maxThuTu);
                anh.setLaAnhChinh(isFirst);
                isFirst = false;
                sanPham.getAnhSanPhams().add(anh);
            }
            sanPhamRepository.save(sanPham);

            // ── SP đơn: set ảnh chính vào anhSanPham của chiTiet ──
            // ── SP đơn: set ảnh chính vào anhSanPham của chiTiet ──
            if (sanPham.getChiTietSanPhams() != null
                    && sanPham.getChiTietSanPhams().size() == 1) {
                ChiTietSanPham chiTiet = sanPham.getChiTietSanPhams().get(0);
                String anhChinh = sanPham.getAnhSanPhams().stream()
                        .filter(a -> Boolean.TRUE.equals(a.getLaAnhChinh()))
                        .map(AnhSanPham::getDuongDanAnh)
                        .findFirst()
                        .orElse(sanPham.getAnhSanPhams().get(0).getDuongDanAnh());
                chiTiet.setAnhSanPham(anhChinh);
                chiTietSanPhamRepository.save(chiTiet);
            }
        }

        return mapToDetailDTO(sanPham);
    }

    /**
     * Upload ảnh cho 1 biến thể cụ thể.
     */
    @Transactional
    public ChiTietSanPhamDTO uploadVariantImage(Long variantId, MultipartFile file) {
        ChiTietSanPham variant = chiTietSanPhamRepository.findById(variantId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy biến thể id: " + variantId));

        String url = fileService.storeFile(file, "products/variants");
        variant.setAnhSanPham(url);
        chiTietSanPhamRepository.save(variant);

        return mapVariantToDTO(variant);
    }

    // ===================================================================================
    // PHẦN 4: XÓA
    // ===================================================================================

    @Transactional
    public void deleteProduct(Long id) {
        SanPham sp = sanPhamRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm: " + id));
        sanPhamRepository.delete(sp);
    }

    @Transactional
    public void deleteBulkProducts(List<Long> ids) {
        List<SanPham> list = sanPhamRepository.findAllById(ids);
        sanPhamRepository.deleteAll(list);
    }

    // ===================================================================================
    // PHẦN 5: MAPPING (Entity <-> DTO)
    // ===================================================================================

    private SanPhamResponseDTO mapToResponseDTO(SanPham entity) {
        SanPhamResponseDTO dto = new SanPhamResponseDTO();
        dto.setId(entity.getId());
        dto.setTenSanPham(entity.getTenSanPham());
        dto.setMoTaNgan(entity.getMoTaNgan());
        dto.setTrangThai(entity.getTrangThai());
        dto.setLaCombo(entity.getLaCombo());
        dto.setLienHe(entity.getLienHe());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setZaloLink(entity.getZaloLink());

        if (entity.getDanhMuc() != null)
            dto.setDanhMuc(entity.getDanhMuc().getTenDanhMuc());

        if (entity.getChiTietSanPhams() != null && !entity.getChiTietSanPhams().isEmpty()) {
            List<ChiTietSanPham> variants = entity.getChiTietSanPhams();
            ChiTietSanPham cheapest = variants.stream()
                    .filter(v -> v.getGiaBan() != null)
                    .min(Comparator.comparingDouble(ChiTietSanPham::getGiaBan))
                    .orElse(variants.get(0));
            dto.setChiTietId(cheapest.getId());
            dto.setMaSku(cheapest.getMaSku());
            dto.setGiaBan(cheapest.getGiaBan());
            dto.setGiaNhap(cheapest.getGiaNhap());
            dto.setGiaGoc(cheapest.getGiaGoc()); // ← THÊM
            dto.setTonKho(variants.stream()
                    .mapToInt(v -> v.getTonKho() != null ? v.getTonKho() : 0).sum());
            dto.setSoLuongBienThe(variants.size());
            variants.stream()
                    .filter(v -> v.getGiaBan() != null)
                    .max(Comparator.comparingDouble(ChiTietSanPham::getGiaBan))
                    .ifPresent(v -> dto.setGiaBanMax(v.getGiaBan()));
        } else {
            dto.setSoLuongBienThe(0);
            dto.setGiaBanMax(dto.getGiaBan());
        }

        if (entity.getAnhSanPhams() != null && !entity.getAnhSanPhams().isEmpty()) {
            String anhChinh = entity.getAnhSanPhams().stream()
                    .filter(a -> Boolean.TRUE.equals(a.getLaAnhChinh()))
                    .map(AnhSanPham::getDuongDanAnh)
                    .findFirst()
                    .orElse(entity.getAnhSanPhams().get(0).getDuongDanAnh());
            dto.setAnhChinh(anhChinh);
        }

        // ── KHÔNG gọi DB ở đây nữa ──
        dto.setDaBan(0);
        dto.setDiemDanhGia(0.0);

        return dto;
    }

    private SanPhamDetailDTO mapToDetailDTO(SanPham entity) {
        SanPhamDetailDTO dto = new SanPhamDetailDTO();
        dto.setId(entity.getId());
        dto.setTenSanPham(entity.getTenSanPham());
        dto.setMoTaNgan(entity.getMoTaNgan());
        dto.setMoTaChiTiet(entity.getMoTaChiTiet());
        dto.setTrangThai(entity.getTrangThai());
        dto.setLaCombo(entity.getLaCombo());

        // ── Field bổ sung ──
        dto.setThuongHieu(entity.getThuongHieu());
        dto.setChatLieu(entity.getChatLieu());
        dto.setKieuDang(entity.getKieuDang());
        dto.setChatLuong(entity.getChatLuong());
        dto.setXuatXu(entity.getXuatXu());
        dto.setTronBo(entity.getTronBo());
        dto.setPhuHopCho(entity.getPhuHopCho());

        // ── Liên hệ ──
        dto.setLienHe(entity.getLienHe());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setZaloLink(entity.getZaloLink());

        // ── Danh mục ──
        if (entity.getDanhMuc() != null) {
            dto.setDanhMucId(entity.getDanhMuc().getId());
            dto.setTenDanhMuc(entity.getDanhMuc().getTenDanhMuc());
        }

        // ── Danh sách biến thể đầy đủ ──
        if (entity.getChiTietSanPhams() != null && !entity.getChiTietSanPhams().isEmpty()) {
            List<ChiTietSanPhamDTO> variantDTOs = entity.getChiTietSanPhams().stream()
                    .map(this::mapVariantToDTO)
                    .collect(Collectors.toList());
            dto.setChiTietSanPhams(variantDTOs);

            ChiTietSanPham first = entity.getChiTietSanPhams().get(0);
            dto.setChiTietId(first.getId());
            dto.setMaSku(first.getMaSku());
            dto.setTrongLuong(first.getTrongLuong());
            dto.setGiaNhap(first.getGiaNhap());
            dto.setGiaBan(first.getGiaBan());
            dto.setTonKho(first.getTonKho());
        }

        // ── Ảnh ──
        if (entity.getAnhSanPhams() != null && !entity.getAnhSanPhams().isEmpty()) {
            List<String> urls = entity.getAnhSanPhams().stream()
                    .map(AnhSanPham::getDuongDanAnh).collect(Collectors.toList());
            dto.setDanhSachAnh(urls);

            String anhChinh = entity.getAnhSanPhams().stream()
                    .filter(a -> Boolean.TRUE.equals(a.getLaAnhChinh()))
                    .map(AnhSanPham::getDuongDanAnh).findFirst()
                    .orElse(urls.get(0));
            dto.setAnhChinh(anhChinh);
        }

        // ── Combo items ──
        // ── Combo items ──
        if (Boolean.TRUE.equals(entity.getLaCombo())
                && entity.getChiTietSanPhams() != null
                && !entity.getChiTietSanPhams().isEmpty()) {

            ChiTietSanPham chiTietCha = entity.getChiTietSanPhams().get(0);
            List<ChiTietCombo> combos = chiTietComboRepository
                    .findByChiTietComboId(chiTietCha.getId());

            if (combos != null && !combos.isEmpty()) {
                List<ChiTietComboDTO> comboDTOs = combos.stream()
                        .filter(c -> Boolean.TRUE.equals(c.getTrangThai()))
                        .map(this::mapComboToDTO)
                        .collect(Collectors.toList());
                dto.setChiTietCombos(comboDTOs);
            }
        }

        return dto;
    }

    private ChiTietSanPhamDTO mapVariantToDTO(ChiTietSanPham entity) {
        ChiTietSanPhamDTO dto = new ChiTietSanPhamDTO();
        dto.setId(entity.getId());
        dto.setMaSku(entity.getMaSku());
        dto.setMauSac(entity.getMauSac());
        dto.setMauSacHex(entity.getMauSacHex());
        dto.setKichThuoc(entity.getKichThuoc());
        dto.setTrongLuong(entity.getTrongLuong());
        dto.setGiaNhap(entity.getGiaNhap());
        dto.setGiaBan(entity.getGiaBan());
        dto.setTonKho(entity.getTonKho());
        dto.setAnhSanPham(entity.getAnhSanPham());
        dto.setTrangThai(entity.getTrangThai());
        dto.setGiaGoc(entity.getGiaGoc()); // ← THÊM
        return dto;
    }

    private ChiTietComboDTO mapComboToDTO(ChiTietCombo entity) {
        ChiTietComboDTO dto = new ChiTietComboDTO();
        dto.setId(entity.getId());
        dto.setTenSanPham(entity.getTenComboItem());
        dto.setAnhSanPham(entity.getAnhComboItem());
        dto.setSoLuong(entity.getSoLuong());
        dto.setTrangThai(entity.getTrangThai());
        return dto;
    }


    @Transactional
    public ChiTietComboDTO uploadComboItemImage(Long comboItemId, MultipartFile file) {
        ChiTietCombo combo = chiTietComboRepository.findById(comboItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy combo item: " + comboItemId));

        String url = fileService.storeFile(file, "products/combo");
        combo.setAnhComboItem(url);  // ← lưu thẳng vào combo
        chiTietComboRepository.save(combo);

        return mapComboToDTO(combo);
    }

    // ── Helper: lấy tất cả id con/cháu (BFS) ──
    private Set<Long> getAllDescendantIds(Long rootId) {
        Set<Long> result = new HashSet<>();
        Queue<Long> queue = new LinkedList<>();
        queue.add(rootId);

        while (!queue.isEmpty()) {
            Long current = queue.poll();
            result.add(current);
            // Lấy danh sách con trực tiếp
            danhMucRepository.findByDanhMucChaId(current)
                    .forEach(child -> {
                        if (!result.contains(child.getId())) {
                            queue.add(child.getId());
                        }
                    });
        }
        return result;
    }

    public SanPhamDetailDTO getProductByChiTietId(Long chiTietSanPhamId) {
        ChiTietSanPham chiTiet = chiTietSanPhamRepository.findById(chiTietSanPhamId)
                .orElseThrow(() -> new RuntimeException(
                        "Không tìm thấy biến thể id: " + chiTietSanPhamId));

        SanPham sanPham = chiTiet.getSanPham();
        if (sanPham == null) {
            throw new RuntimeException(
                    "Biến thể id " + chiTietSanPhamId + " không gắn với sản phẩm nào");
        }

        SanPhamDetailDTO dto = mapToDetailDTO(sanPham);

        // Lấy thêm stats (daBan, rating) giống getProductById
        Integer daBan = chiTietDonHangRepository.countTotalSoldBySanPhamId(sanPham.getId());
        dto.setDaBan(daBan != null ? daBan : 0);

        Double saoTB = danhGiaRepository.getAverageStarsByProductId(sanPham.getId());
        dto.setDiemDanhGia(saoTB != null ? Math.round(saoTB * 10.0) / 10.0 : 0.0);

        Long tongDG = danhGiaRepository.countTotalReviewsByProductId(sanPham.getId());
        dto.setSoLuotDanhGia(tongDG != null ? tongDG : 0L);

        dto.setReviews(danhGiaRepository.findApprovedReviewsByProductId(sanPham.getId()));

        return dto;
    }

}