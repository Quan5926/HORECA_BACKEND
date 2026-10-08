package com.example.horecaone.service.banner;

import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileService {

    // Thư mục gốc chung
    private final String UPLOAD_ROOT = "C:/Users/Administrator/uploads/";
    /**
     * Hàm lưu file dùng chung
     * @param file: File từ client gửi lên
     * @param subFolder: Thư mục con (VD: "banners" hoặc "products")
     * @return Đường dẫn file để lưu vào DB (VD: "/uploads/products/abc.jpg")
     */
    public String storeFile(MultipartFile file, String subFolder) {
        try {
            // 1. Xác định đường dẫn thư mục lưu trữ
            String targetDir = UPLOAD_ROOT + subFolder + "/";
            Path uploadPath = Paths.get(targetDir);

            // 2. Tạo thư mục nếu chưa tồn tại
            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 3. Tạo tên file duy nhất bằng UUID
            String originalFilename = file.getOriginalFilename();
            String fileExtension = "";
            if (originalFilename != null && originalFilename.contains(".")) {
                fileExtension = originalFilename.substring(originalFilename.lastIndexOf("."));
            }
            String newFilename = UUID.randomUUID().toString() + fileExtension;

            // 4. Lưu file vật lý
            Path filePath = uploadPath.resolve(newFilename);
            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 5. Trả về URL (VD: "/uploads/banners/uuid.png")
            return "/uploads/" + subFolder + "/" + newFilename;

        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi lưu file: " + e.getMessage());
        }
    }

    /**
     * Hàm xóa file vật lý
     */
    public void deleteFile(String fileUrl) {
        try {
            if (fileUrl == null || fileUrl.isEmpty()) return;
            // Xóa dấu / ở đầu để trỏ đúng đường dẫn vật lý
            String pathStr = fileUrl.startsWith("/") ? fileUrl.substring(1) : fileUrl;
            Path path = Paths.get(pathStr);
            Files.deleteIfExists(path);
        } catch (IOException e) {
            System.err.println("Không thể xóa file: " + e.getMessage());
        }
    }
}