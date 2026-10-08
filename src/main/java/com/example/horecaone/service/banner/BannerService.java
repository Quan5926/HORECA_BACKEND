package com.example.horecaone.service.banner;

import com.example.horecaone.dto.banner.BannerDTO;

import java.util.List;

public interface BannerService {
    List<BannerDTO> getPublicBanners(String viTri); // Cho User
    List<BannerDTO> getAllBannersAdmin();           // Cho Admin
    BannerDTO createBanner(BannerDTO bannerDTO);
    BannerDTO updateBanner(Long id, BannerDTO bannerDTO);
    void deleteBanner(Long id);
    BannerDTO toggleStatus(Long id);
}