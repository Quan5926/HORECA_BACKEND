package com.example.horecaone.dto.categories;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DanhMucResponseDTO {
    private Long id;
    private String name;        // Map với TEN_DANH_MUC
    private String desc;        // Map với MO_TA
    private String image;       // Map với ANH_DAI_DIEN
    private Boolean status;     // Map với TRANG_THAI
    private Long count;         // Số lượng sản phẩm thuộc danh mục này
    private Long danhMucChaId;
}