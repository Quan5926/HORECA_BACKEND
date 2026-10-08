package com.example.horecaone.dto.reviews;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor // Bắt buộc phải có
public class ReviewDTO {
    private Long id;              // 1 (d.id)
    private String loai;          // 2 (d.loai)
    private Long baiVietId;       // 3 (d.baiVietId)
    private String tenBaiViet;    // 4 (Dấu '' trong query)
    private String hoTen;         // 5 (nd.hoTen)
    private Integer diemDanhGia;  // 6 (d.diemDanhGia)
    private String noiDung;       // 7 (d.noiDung)
    private Boolean daDuyet;      // 8 (d.daDuyet - khớp kiểu Boolean của Entity)
    private LocalDateTime ngayTao;// 9 (d.ngayTao)
    private String hinhAnh;
}