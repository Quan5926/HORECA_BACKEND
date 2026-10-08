package com.example.horecaone.dto.contact;

import lombok.Data;

@Data
public class CompanyInfoDto {
    private String name;    // Map với TEN_CONG_TY
    private String phone;   // Map với SO_DIEN_THOAI
    private String email;
    private String office;  // Map với VAN_PHONG
    private String factory; // Map với NHA_MAY
    private String mapUrl;
    private Long nguoiSua;  // Tùy chọn ID Admin đang sửa
}