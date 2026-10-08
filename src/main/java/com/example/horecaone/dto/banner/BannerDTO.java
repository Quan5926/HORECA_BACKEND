package com.example.horecaone.dto.banner;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BannerDTO {
    private Long id;
    private String tieuDe;
    private String duongDanAnh;
    private String lienKet;
    private Integer thuTu;
    private Boolean trangThai;
    private String viTri;
}