package com.example.horecaone.service.categories;


import com.example.horecaone.dto.categories.DanhMucResponseDTO;
import com.example.horecaone.entity.categories.DanhMuc;

import java.util.List;

public interface DanhMucService {
    List<DanhMucResponseDTO> getAllDanhMucs();
    DanhMuc getDanhMucById(Long id);
    DanhMuc createDanhMuc(DanhMuc danhMuc);
    DanhMuc updateDanhMuc(Long id, DanhMuc danhMucDetails);
    void deleteDanhMuc(Long id);
    void deleteMultipleDanhMucs(List<Long> ids); // Cho chức năng xóa nhiều
}