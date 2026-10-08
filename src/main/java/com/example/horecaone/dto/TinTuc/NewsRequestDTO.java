package com.example.horecaone.dto.TinTuc;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class NewsRequestDTO {
    @JsonProperty("tieuDe")     // Bắt buộc phải khớp với tên biến gửi từ Vue
    private String title;

    @JsonProperty("moTaNgan")
    private String desc;

    @JsonProperty("noiDung")
    private String content;

    @JsonProperty("anhDaiDien")
    private String image;

    @JsonProperty("danhMuc")
    private String category;

    @JsonProperty("noiBat")
    private Boolean isFeatured;

    @JsonProperty("tags")
    private String tags;

    // THÊM 2 DÒNG NÀY ĐỂ HỨNG TRẠNG THÁI TỪ VUE NHÉ
    @JsonProperty("trangThai")
    private Boolean trangThai;
}