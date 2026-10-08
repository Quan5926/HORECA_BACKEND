package com.example.horecaone.service.banner;

import com.example.horecaone.dto.banner.BannerDTO;
import com.example.horecaone.entity.banner.Banner;
import com.example.horecaone.repository.banner.BannerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class BannerServiceImpl implements BannerService {

    private final BannerRepository bannerRepository;

    @Override
    public List<BannerDTO> getPublicBanners(String viTri) {
        List<Banner> banners = bannerRepository.findByTrangThaiTrueAndViTriOrderByThuTuAsc(viTri);
        return banners.stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public List<BannerDTO> getAllBannersAdmin() {
        return bannerRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    @Override
    public BannerDTO createBanner(BannerDTO dto) {
        Banner banner = new Banner();
        banner.setTieuDe(dto.getTieuDe());
        banner.setDuongDanAnh(dto.getDuongDanAnh());
        banner.setLienKet(dto.getLienKet());
        banner.setThuTu(dto.getThuTu());
        banner.setTrangThai(dto.getTrangThai() != null ? dto.getTrangThai() : true);
        banner.setViTri(dto.getViTri());
        // banner.setNguoiTao(...) -> Gắn ID user đang login từ Security Context vào đây sau

        return mapToDTO(bannerRepository.save(banner));
    }

    @Override
    public BannerDTO updateBanner(Long id, BannerDTO dto) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Banner ID: " + id));

        banner.setTieuDe(dto.getTieuDe());
        // Nếu FE gửi ảnh mới thì mới update đường dẫn ảnh
        if(dto.getDuongDanAnh() != null && !dto.getDuongDanAnh().isEmpty()){
            banner.setDuongDanAnh(dto.getDuongDanAnh());
        }
        banner.setLienKet(dto.getLienKet());
        banner.setThuTu(dto.getThuTu());
        banner.setTrangThai(dto.getTrangThai());
        banner.setViTri(dto.getViTri());

        return mapToDTO(bannerRepository.save(banner));
    }

    @Override
    public void deleteBanner(Long id) {
        bannerRepository.deleteById(id);
    }

    @Override
    public BannerDTO toggleStatus(Long id) {
        Banner banner = bannerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy Banner"));
        banner.setTrangThai(!banner.getTrangThai());
        return mapToDTO(bannerRepository.save(banner));
    }

    // Hàm tiện ích chuyển đổi Entity -> DTO
    private BannerDTO mapToDTO(Banner banner) {
        return new BannerDTO(
                banner.getId(),
                banner.getTieuDe(),
                banner.getDuongDanAnh(),
                banner.getLienKet(),
                banner.getThuTu(),
                banner.getTrangThai(),
                banner.getViTri()
        );
    }
}