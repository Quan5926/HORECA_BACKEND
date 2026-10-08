package com.example.horecaone.controller.categories;

import com.example.horecaone.dto.categories.DanhMucResponseDTO;
import com.example.horecaone.service.categories.DanhMucService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/categories") // Đường dẫn này đã được permitAll() trong Security

@RequiredArgsConstructor // Thêm annotation này để Spring tự động inject DanhMucService
public class PublicDanhMucController {

    private final DanhMucService danhMucService;

    // Viết API GET lấy danh sách danh mục đang active cho User
    @GetMapping
    public ResponseEntity<List<DanhMucResponseDTO>> getActiveCategories() {
        // Lấy toàn bộ danh mục từ Service hiện có
        List<DanhMucResponseDTO> allCategories = danhMucService.getAllDanhMucs();

        // TỐI ƯU: Lọc chỉ lấy các danh mục đang Active (status = true) ngay tại Backend
        // Giả sử trong DanhMucResponseDTO của bạn có hàm getStatus() hoặc isStatus()
        List<DanhMucResponseDTO> activeCategories = allCategories.stream()
                .filter(category -> category.getStatus() != null && category.getStatus())
                .collect(Collectors.toList());

        // Nếu DTO chưa có getStatus(), bạn có thể tạm return luôn allCategories
        // return ResponseEntity.ok(allCategories);
        // (Vì trên Frontend mình cũng đã viết hàm filter rào lại một lớp rồi)

        return ResponseEntity.ok(activeCategories);
    }
}
