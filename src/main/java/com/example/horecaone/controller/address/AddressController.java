package com.example.horecaone.controller.address;

import com.example.horecaone.dto.address.AddressRequestDTO;
import com.example.horecaone.entity.address.DiaChiGiaoHang;
import com.example.horecaone.repository.login.NguoiDungRepository;
import com.example.horecaone.security.login.JwtUtil;
import com.example.horecaone.service.address.AddressService;
// ← đổi đúng tên repo User của bạn
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/addresses")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private NguoiDungRepository nguoiDungRepository; // ← đổi đúng tên repo User của bạn

    // ✅ Lấy userId từ email trong JWT → lookup DB
    private Long getCurrentUserId(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Chưa đăng nhập hoặc token không hợp lệ!");
        }

        String token = authHeader.substring(7);

        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token không hợp lệ hoặc đã hết hạn!");
        }

        // JWT của bạn dùng email làm subject
        String email = jwtUtil.extractEmail(token);

        // Lookup userId từ email
        return nguoiDungRepository.findByEmail(email)
                .map(user -> user.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng với email: " + email));
    }

    @PostMapping
    public ResponseEntity<?> addAddress(
            @Valid @RequestBody AddressRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            DiaChiGiaoHang saved = addressService.createAddress(userId, request);
            return ResponseEntity.ok(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequestDTO request,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            DiaChiGiaoHang updated = addressService.updateAddress(userId, id, request);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @GetMapping
    public ResponseEntity<?> getUserAddresses(HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            List<DiaChiGiaoHang> addresses = addressService.getUserAddresses(userId);
            return ResponseEntity.ok(addresses);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteAddress(
            @PathVariable Long id,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getCurrentUserId(httpRequest);
            addressService.deleteAddress(userId, id);
            return ResponseEntity.ok("Xóa địa chỉ thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}