package com.example.horecaone.dto.contact;

import lombok.Data;

@Data
public class ContactStatusRequest {
    private String trangThai;
    private String ghiChuAdmin;
    private Long nguoiSua; // Tùy chọn truyền từ FE (ID Admin đang đăng nhập)
}