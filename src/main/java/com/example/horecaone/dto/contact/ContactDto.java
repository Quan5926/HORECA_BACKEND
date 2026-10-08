package com.example.horecaone.dto.contact;

import lombok.Data;

@Data
public class ContactDto {
    private Long id;
    private String hoTen;
    private String email;
    private String soDienThoai;
    private String tieuDe;
    private String noiDung;
    private String trangThai;
    private String ghiChuAdmin;
    private String ngayTao;
}