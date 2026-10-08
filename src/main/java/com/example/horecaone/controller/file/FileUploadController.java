package com.example.horecaone.controller.file;

import com.example.horecaone.service.banner.FileService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/upload")
@RequiredArgsConstructor
public class FileUploadController {

    private final FileService fileService;

    @PostMapping
    public ResponseEntity<Map<String, String>> uploadFile(@RequestParam("file") MultipartFile file) {

        // Gọi Service lưu file và lấy đường dẫn về
        String fileUrl = fileService.storeFile(file, "products");

        // Trả về một object JSON có chứa url: { "url": "/uploads/ten-anh-moi.jpg" }
        Map<String, String> response = new HashMap<>();
        response.put("url", fileUrl);

        return ResponseEntity.ok(response);
    }
}
