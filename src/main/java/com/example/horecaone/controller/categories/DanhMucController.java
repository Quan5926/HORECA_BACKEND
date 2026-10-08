package com.example.horecaone.controller.categories;

import com.example.horecaone.dto.categories.DanhMucResponseDTO;
import com.example.horecaone.entity.categories.DanhMuc;
import com.example.horecaone.service.categories.DanhMucService;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/admin/categories")
@RequiredArgsConstructor
public class DanhMucController {

    private final DanhMucService danhMucService;

    // Lấy danh sách (có kèm số lượng SP)
    @GetMapping
    public ResponseEntity<List<DanhMucResponseDTO>> getAll() {
        return ResponseEntity.ok(danhMucService.getAllDanhMucs());
    }

    // Lấy chi tiết 1 danh mục
    @GetMapping("/{id}")
    public ResponseEntity<DanhMuc> getById(@PathVariable Long id) {
        return ResponseEntity.ok(danhMucService.getDanhMucById(id));
    }

    // Thêm mới
    @PostMapping
    public ResponseEntity<DanhMuc> create(@RequestBody DanhMuc danhMuc) {
        return ResponseEntity.ok(danhMucService.createDanhMuc(danhMuc));
    }

    // Cập nhật
    @PutMapping("/{id}")
    public ResponseEntity<DanhMuc> update(@PathVariable Long id, @RequestBody DanhMuc danhMuc) {
        return ResponseEntity.ok(danhMucService.updateDanhMuc(id, danhMuc));
    }

    // Xóa 1 item
    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable Long id) {
        try {
            danhMucService.deleteDanhMuc(id);
            return ResponseEntity.noContent().build();
        } catch (DataIntegrityViolationException e) {
            // Bắt lỗi khóa ngoại (do còn sản phẩm bên trong)
            return ResponseEntity.badRequest().body("Danh mục này đang chứa sản phẩm! Vui lòng chuyển sang trạng thái 'Đang ẩn' hoặc xóa hết sản phẩm trước khi xóa.");
        } catch (Exception e) {
            return ResponseEntity.internalServerError().body("Lỗi hệ thống khi xóa danh mục.");
        }
    }

    // Xóa nhiều items (Bulk Delete) - Nhận mảng [1, 2, 3] từ VueJS
    @DeleteMapping("/bulk")
    public ResponseEntity<Void> deleteMultiple(@RequestBody List<Long> ids) {
        danhMucService.deleteMultipleDanhMucs(ids);
        return ResponseEntity.noContent().build();
    }



    @PostMapping("/upload")
// Đổi ResponseEntity<String> thành ResponseEntity<Map<String, String>>
    public ResponseEntity<Map<String, String>> uploadImage(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }

        try {
            Path uploadPath = Paths.get("uploads/");
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            String originalFilename = file.getOriginalFilename();
            String uniqueFileName = UUID.randomUUID().toString() + "_" + originalFilename;
            Path filePath = uploadPath.resolve(uniqueFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            String fileUrl = "http://localhost:8080/uploads/" + uniqueFileName;

            // Bọc URL vào một Map (JSON) để trả về an toàn tuyệt đối
            Map<String, String> response = new HashMap<>();
            response.put("url", fileUrl);

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
