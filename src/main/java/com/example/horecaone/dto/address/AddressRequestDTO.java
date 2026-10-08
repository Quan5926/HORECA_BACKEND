package com.example.horecaone.dto.address;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressRequestDTO {

    @NotBlank(message = "Họ tên người nhận không được để trống")
    @Size(max = 200, message = "Họ tên không được vượt quá 200 ký tự")
    private String tenNguoiNhan;

    @NotBlank(message = "Số điện thoại không được để trống")
    @Pattern(regexp = "^(0|\\+84)(\\s|\\.)?((3[2-9])|(5[689])|(7[06-9])|(8[1-689])|(9[0-46-9]))(\\d)(\\s|\\.)?(\\d{3})(\\s|\\.)?(\\d{3})$",
            message = "Số điện thoại không đúng định dạng Việt Nam")
    private String sdt;

    @NotBlank(message = "Tỉnh/Thành phố không được để trống")
    private String tinhThanh;

    @NotBlank(message = "Quận/Huyện không được để trống")
    private String quanHuyen;

    @NotBlank(message = "Phường/Xã không được để trống")
    private String phuongXa;

    @NotBlank(message = "Địa chỉ chi tiết không được để trống")
    private String diaChiChiTiet;

    private Boolean laMacDinh;
}