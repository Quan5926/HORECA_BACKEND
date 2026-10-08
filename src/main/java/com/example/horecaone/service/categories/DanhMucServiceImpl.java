package com.example.horecaone.service.categories;

import com.example.horecaone.dto.categories.DanhMucResponseDTO;
import com.example.horecaone.entity.categories.DanhMuc;
import com.example.horecaone.repository.categories.DanhMucRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DanhMucServiceImpl implements DanhMucService {

    private final DanhMucRepository danhMucRepository;

    @Override
    public List<DanhMucResponseDTO> getAllDanhMucs() {
        return danhMucRepository.findAllWithProductCount();
    }

    @Override
    public DanhMuc getDanhMucById(Long id) {
        return danhMucRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục với ID: " + id));
    }

    @Override
    @Transactional
    public DanhMuc createDanhMuc(DanhMuc danhMuc) {
        // Mặc định trạng thái là true (1) nếu FE không gửi
        if (danhMuc.getTrangThai() == null) danhMuc.setTrangThai(true);
        return danhMucRepository.save(danhMuc);
    }

    @Override
    @Transactional
    public DanhMuc updateDanhMuc(Long id, DanhMuc danhMucDetails) {
        DanhMuc existing = getDanhMucById(id);
        existing.setTenDanhMuc(danhMucDetails.getTenDanhMuc());
        existing.setMoTa(danhMucDetails.getMoTa());
        existing.setAnhDaiDien(danhMucDetails.getAnhDaiDien());
        existing.setTrangThai(danhMucDetails.getTrangThai());

        // BỔ SUNG: Cập nhật danh mục cha
        existing.setDanhMucChaId(danhMucDetails.getDanhMucChaId());

        // Có thể set người sửa tại đây nếu có JWT User
        return danhMucRepository.save(existing);
    }

    @Override
    @Transactional
    public void deleteDanhMuc(Long id) {
        try {
            danhMucRepository.deleteById(id);
        } catch (DataIntegrityViolationException e) {
            // Bắt lỗi khóa ngoại
            throw new RuntimeException("Không thể xóa danh mục này vì vẫn còn sản phẩm bên trong!");
        }
    }

    @Override
    @Transactional
    public void deleteMultipleDanhMucs(List<Long> ids) {
        danhMucRepository.deleteAllByIdInBatch(ids);
    }
}