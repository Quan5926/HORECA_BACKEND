package com.example.horecaone.dto.cart;

import lombok.Data;
import java.util.List;

@Data
public class CartResponseDTO {
    private Long id; // ID của bảng GIO_HANG
    private Long nguoiDungId;
    private List<CartItemResponseDTO> chiTietGioHangs; // Danh sách các sản phẩm (đã đầy đủ thông tin)
}