package com.example.horecaone.controller.cart;

import com.example.horecaone.dto.cart.AddToCartReq;
import com.example.horecaone.dto.cart.CartResponseDTO;
import com.example.horecaone.dto.cart.CheckoutReq;
import com.example.horecaone.dto.cart.UpdateCartReq;
import com.example.horecaone.entity.cart.DonHang;
import com.example.horecaone.entity.cart.GioHang;
import com.example.horecaone.repository.login.NguoiDungRepository;
import com.example.horecaone.security.login.JwtUtil;
import com.example.horecaone.service.cart.CartAndOrderService;
 // ← đổi đúng tên repo của bạn
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1")

public class CartOrderController {

    @Autowired private CartAndOrderService cartAndOrderService;
    @Autowired private JwtUtil jwtUtil;
    @Autowired private NguoiDungRepository nguoiDungRepository;

    // ✅ Helper: lấy userId từ JWT token
    private Long getUserIdFromRequest(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            throw new RuntimeException("Chưa đăng nhập!");
        }
        String token = authHeader.substring(7);
        if (!jwtUtil.validateToken(token)) {
            throw new RuntimeException("Token không hợp lệ hoặc hết hạn!");
        }
        String email = jwtUtil.extractEmail(token);
        return nguoiDungRepository.findByEmail(email)
                .map(u -> u.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy người dùng!"));
    }

    // ✅ Thêm vào giỏ — override nguoiDungId từ token, không tin FE truyền lên
    @PostMapping("/cart/add")
    public ResponseEntity<?> addToCart(
            @RequestBody AddToCartReq req,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            req.setNguoiDungId(userId); // ← luôn dùng userId từ token
            GioHang cart = cartAndOrderService.addToCart(req);
            return ResponseEntity.ok(cart);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi thêm vào giỏ: " + e.getMessage());
        }
    }

    // ✅ Lấy giỏ hàng — chỉ lấy của chính mình
    @GetMapping("/cart/{userId}")
    public ResponseEntity<?> getCart(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {
        try {
            Long tokenUserId = getUserIdFromRequest(httpRequest);
            // Bảo mật: chỉ cho lấy giỏ hàng của chính mình
            if (!tokenUserId.equals(userId)) {
                return ResponseEntity.status(403).body("Không có quyền truy cập giỏ hàng này!");
            }
            CartResponseDTO cartDTO = cartAndOrderService.getCartByUserId(tokenUserId);
            if (cartDTO == null) return ResponseEntity.ok().build();
            return ResponseEntity.ok(cartDTO);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi lấy giỏ hàng: " + e.getMessage());
        }
    }

    // ✅ Checkout — lấy userId từ token
    @PostMapping("/order/checkout")
    public ResponseEntity<?> placeOrder(
            @RequestBody CheckoutReq req,
            HttpServletRequest httpRequest) {
        try {
            Long userId = getUserIdFromRequest(httpRequest);
            req.setNguoiDungId(userId); // ← override, không tin FE
            DonHang order = cartAndOrderService.placeOrder(req);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi đặt hàng: " + e.getMessage());
        }
    }

    // ✅ Xóa sản phẩm khỏi giỏ — verify token trước
    @DeleteMapping("/cart/remove/{cartItemId}")
    public ResponseEntity<?> removeCartItem(
            @PathVariable Long cartItemId,
            HttpServletRequest httpRequest) {
        try {
            getUserIdFromRequest(httpRequest); // chỉ cần verify token hợp lệ
            cartAndOrderService.removeCartItem(cartItemId);
            return ResponseEntity.ok("Xóa sản phẩm thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi khi xóa: " + e.getMessage());
        }
    }

    // ✅ Lấy đơn hàng — chỉ lấy của chính mình
    @GetMapping("/order/user/{userId}")
    public ResponseEntity<?> getOrdersByUser(
            @PathVariable Long userId,
            HttpServletRequest httpRequest) {
        try {
            Long tokenUserId = getUserIdFromRequest(httpRequest);
            if (!tokenUserId.equals(userId)) {
                return ResponseEntity.status(403).body("Không có quyền xem đơn hàng này!");
            }
            List<DonHang> orders = cartAndOrderService.getOrdersByUserId(tokenUserId);
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi lấy danh sách đơn hàng: " + e.getMessage());
        }
    }

    @PutMapping("/cart/update/{cartItemId}")
    public ResponseEntity<?> updateCartItem(
            @PathVariable Long cartItemId,
            @RequestBody UpdateCartReq req,
            HttpServletRequest httpRequest) {
        try {
            getUserIdFromRequest(httpRequest); // verify token
            cartAndOrderService.updateCartItemQty(cartItemId, req.getSoLuong());
            return ResponseEntity.ok("Cập nhật số lượng thành công");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body("Lỗi cập nhật: " + e.getMessage());
        }
    }
}
