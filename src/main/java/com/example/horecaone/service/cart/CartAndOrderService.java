package com.example.horecaone.service.cart;

import com.example.horecaone.dto.cart.AddToCartReq;
import com.example.horecaone.dto.cart.CartItemResponseDTO;
import com.example.horecaone.dto.cart.CartResponseDTO;
import com.example.horecaone.dto.cart.CheckoutReq;
import com.example.horecaone.entity.cart.ChiTietDonHang;
import com.example.horecaone.entity.cart.ChiTietGioHang;
import com.example.horecaone.entity.cart.DonHang;
import com.example.horecaone.entity.cart.GioHang;
import com.example.horecaone.entity.product.AnhSanPham;
import com.example.horecaone.entity.product.ChiTietSanPham;
import com.example.horecaone.repository.cart.ChiTietGioHangRepository;
import com.example.horecaone.repository.cart.DonHangRepository;
import com.example.horecaone.repository.cart.GioHangRepository;
import com.example.horecaone.repository.product.ChiTietSanPhamRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class CartAndOrderService {

    @Autowired private GioHangRepository gioHangRepo;
    @Autowired private ChiTietGioHangRepository chiTietGioHangRepo;
    @Autowired private DonHangRepository donHangRepo;
    @Autowired private ChiTietSanPhamRepository chiTietSanPhamRepo;
    // --- THÊM VÀO GIỎ HÀNG ---
    @Transactional
    public GioHang addToCart(AddToCartReq req) {
        // 1. Tìm hoặc tạo mới Giỏ hàng
        GioHang cart = null;
        if (req.getNguoiDungId() != null) {
            cart = gioHangRepo.findByNguoiDungId(req.getNguoiDungId()).orElse(new GioHang());
            cart.setNguoiDungId(req.getNguoiDungId());
        } else {
            cart = gioHangRepo.findByMaPhien(req.getMaPhien()).orElse(new GioHang());
            cart.setMaPhien(req.getMaPhien());
        }
        cart = gioHangRepo.save(cart);

        // 2. Kiểm tra sản phẩm đã có trong giỏ chưa
        ChiTietGioHang item = chiTietGioHangRepo.findByGioHangIdAndChiTietSanPhamId(cart.getId(), req.getChiTietSanPhamId())
                .orElse(new ChiTietGioHang());

        if (item.getId() != null) {
            // Đã có -> Cộng dồn số lượng
            item.setSoLuong(item.getSoLuong() + req.getSoLuong());
        } else {
            // Chưa có -> Tạo mới
            item.setGioHang(cart);
            item.setChiTietSanPhamId(req.getChiTietSanPhamId());
            item.setSoLuong(req.getSoLuong());
            item.setGiaKhiThem(req.getGiaHienTai());
            item.setDuocChon(true); // Mặc định chọn khi mới thêm
        }

        chiTietGioHangRepo.save(item);
        return cart;
    }

    // --- THANH TOÁN (TẠO ĐƠN HÀNG) ---
    @Transactional
    public DonHang placeOrder(CheckoutReq req) {
        // 1. Lấy ra các sản phẩm trong giỏ được chọn (Mô phỏng lấy list từ Repo)
        List<ChiTietGioHang> selectedItems = chiTietGioHangRepo.findAllById(req.getChiTietGioHangIds());
        if (selectedItems.isEmpty()) throw new RuntimeException("Chưa chọn sản phẩm nào để thanh toán!");

        // 2. Tính toán tiền
        BigDecimal tongTienTruocGiam = selectedItems.stream()
                .map(item -> item.getGiaKhiThem().multiply(BigDecimal.valueOf(item.getSoLuong())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        // (Chỗ này bạn sẽ gọi Service check Voucher để trừ tiềnGiamGia)
        BigDecimal tienGiamGia = BigDecimal.ZERO;

        BigDecimal tongThanhToan = tongTienTruocGiam.subtract(tienGiamGia).add(req.getPhiVanChuyen());

        // 3. Tạo Đơn hàng
        DonHang order = new DonHang();
        order.setNguoiDungId(req.getNguoiDungId());
        order.setMaDonHang("DH" + UUID.randomUUID().toString().substring(0, 8).toUpperCase());
        order.setTongTienTruocGiam(tongTienTruocGiam);
        order.setTienGiamGia(tienGiamGia);
        order.setPhiVanChuyen(req.getPhiVanChuyen());
        order.setTongTienThanhToan(tongThanhToan);
        order.setTrangThai("CHO_XAC_NHAN");
        order.setPhuongThucThanhToan(req.getPhuongThucThanhToan());
        order.setTrangThaiThanhToan("CHO_XU_LY");
        order.setDiaChiGiaoHangId(req.getDiaChiGiaoHangId());
        order.setGhiChu(req.getGhiChu());
        order.setMaVoucherApDung(req.getMaVoucher());

// 4. Chuyển từ Chi tiết giỏ hàng sang Chi tiết đơn hàng
        List<ChiTietDonHang> danhSachChiTiet = new ArrayList<>();
        for (ChiTietGioHang item : selectedItems) {
            ChiTietDonHang chiTietDH = new ChiTietDonHang();
            chiTietDH.setDonHang(order); // Gắn chi tiết này vào đơn hàng
            chiTietDH.setChiTietSanPhamId(item.getChiTietSanPhamId());
            chiTietDH.setSoLuong(item.getSoLuong());
            chiTietDH.setGiaBan(item.getGiaKhiThem());
            chiTietDH.setThanhTien(item.getGiaKhiThem().multiply(BigDecimal.valueOf(item.getSoLuong())));

            // Thêm vào danh sách thay vì lưu từng cái
            danhSachChiTiet.add(chiTietDH);
        }

// Gắn danh sách chi tiết vào đơn hàng
        order.setChiTietDonHangs(danhSachChiTiet);

// LƯU 1 LẦN DUY NHẤT: Nó sẽ tự động lưu cả Đơn hàng lẫn Chi tiết đơn hàng
// (Nhờ cấu hình CascadeType.ALL ở file Entity)
        order = donHangRepo.save(order);

// 5. Xóa các sản phẩm đã mua khỏi giỏ hàng
        chiTietGioHangRepo.deleteAll(selectedItems);

        return order;
    }

    public CartResponseDTO getCartByUserId(Long userId) {
        GioHang cart = gioHangRepo.findByNguoiDungId(userId).orElse(null);
        if (cart == null) return null;

        CartResponseDTO cartDTO = new CartResponseDTO();
        cartDTO.setId(cart.getId());
        cartDTO.setNguoiDungId(cart.getNguoiDungId());

        List<CartItemResponseDTO> itemDTOs = new ArrayList<>();

        for (ChiTietGioHang item : cart.getChiTietGioHangs()) {
            CartItemResponseDTO itemDTO = new CartItemResponseDTO();
            itemDTO.setId(item.getId());
            itemDTO.setChiTietId(item.getChiTietSanPhamId());
            itemDTO.setSoLuong(item.getSoLuong());
            itemDTO.setDuocChon(item.getDuocChon());
            itemDTO.setGiaBan(item.getGiaKhiThem());

            ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(item.getChiTietSanPhamId()).orElse(null);
            if (ctsp != null) {
                if (ctsp.getSanPham() != null) {
                    itemDTO.setTenSanPham(ctsp.getSanPham().getTenSanPham());
                    itemDTO.setSanPhamId(ctsp.getSanPham().getId());

                    // ── ẢNH: ưu tiên ảnh riêng của biến thể, fallback ảnh sản phẩm ──
                    String urlAnh = null;

                    // 1. Ảnh riêng của biến thể (đúng màu/size)
                    if (ctsp.getAnhSanPham() != null && !ctsp.getAnhSanPham().isBlank()) {
                        urlAnh = ctsp.getAnhSanPham();
                    }

                    // 2. Fallback: ảnh chính của sản phẩm
                    if (urlAnh == null
                            && ctsp.getSanPham().getAnhSanPhams() != null
                            && !ctsp.getSanPham().getAnhSanPhams().isEmpty()) {
                        for (AnhSanPham anh : ctsp.getSanPham().getAnhSanPhams()) {
                            if (Boolean.TRUE.equals(anh.getLaAnhChinh())) {
                                urlAnh = anh.getDuongDanAnh();
                                break;
                            }
                        }
                        // 3. Fallback cuối: ảnh đầu tiên
                        if (urlAnh == null) {
                            urlAnh = ctsp.getSanPham().getAnhSanPhams().get(0).getDuongDanAnh();
                        }
                    }

                    itemDTO.setAnhSanPham(urlAnh);

                } else {
                    itemDTO.setTenSanPham("Sản phẩm không xác định");
                    itemDTO.setAnhSanPham(null);
                }

                // Thông tin từ chi tiết sản phẩm
                itemDTO.setTonKho(ctsp.getTonKho());
                itemDTO.setTrongLuong(ctsp.getTrongLuong());
                itemDTO.setGiaGoc(ctsp.getGiaBan() > 0
                        ? BigDecimal.valueOf(ctsp.getGiaBan())
                        : BigDecimal.ZERO);

                // Biến thể — KHÔNG trả maSku vì không hiện với khách
                itemDTO.setMauSac(ctsp.getMauSac());
                itemDTO.setKichThuoc(ctsp.getKichThuoc());
            }

            itemDTOs.add(itemDTO);
        }

        cartDTO.setChiTietGioHangs(itemDTOs);
        return cartDTO;
    }
    // THÊM HÀM NÀY VÀO SERVICE
    @Transactional
    public void removeCartItem(Long cartItemId) {
        // Xóa sản phẩm khỏi bảng CHI_TIET_GIO_HANG
        chiTietGioHangRepo.deleteById(cartItemId);
    }


    public List<DonHang> getOrdersByUserId(Long userId) {
        // Lấy danh sách đơn hàng gốc từ DB
        List<DonHang> donHangs = donHangRepo.findByNguoiDungIdOrderByIdDesc(userId);

        // Duyệt qua từng đơn hàng để "nhồi" thêm tên và ảnh vào ChiTietDonHang
        for (DonHang donHang : donHangs) {
            if (donHang.getChiTietDonHangs() != null) {
                for (ChiTietDonHang chiTiet : donHang.getChiTietDonHangs()) {

                    // Lôi cổ thông tin Sản Phẩm ra từ DB dựa vào chiTietSanPhamId
                    ChiTietSanPham ctsp = chiTietSanPhamRepo.findById(chiTiet.getChiTietSanPhamId()).orElse(null);

                    if (ctsp != null && ctsp.getSanPham() != null) {
                        chiTiet.setSanPhamId(ctsp.getSanPham().getId());
                        // 1. Nhồi Tên Sản Phẩm vào một biến tạm (@Transient) trong Entity ChiTietDonHang
                        // LƯU Ý: Đảm bảo trong Entity ChiTietDonHang của bạn có khai báo 2 thuộc tính:
                        // @Transient private String tenSanPham;
                        // @Transient private String anhSanPham;
                        chiTiet.setTenSanPham(ctsp.getSanPham().getTenSanPham());

                        // 2. Nhồi Ảnh Sản Phẩm y hệt như cách bạn làm ở Giỏ hàng
                        String urlAnhChinh = null;
                        if (ctsp.getSanPham().getAnhSanPhams() != null && !ctsp.getSanPham().getAnhSanPhams().isEmpty()) {
                            for (AnhSanPham anh : ctsp.getSanPham().getAnhSanPhams()) {
                                if (Boolean.TRUE.equals(anh.getLaAnhChinh())) {
                                    urlAnhChinh = anh.getDuongDanAnh();
                                    break;
                                }
                            }
                            if (urlAnhChinh == null) {
                                urlAnhChinh = ctsp.getSanPham().getAnhSanPhams().get(0).getDuongDanAnh();
                            }
                        }
                        chiTiet.setAnhSanPham(urlAnhChinh);
                    } else {
                        chiTiet.setTenSanPham("Sản phẩm không xác định");
                        chiTiet.setAnhSanPham(null);
                    }
                }
            }
        }
        return donHangs;
    }


    @Transactional
    public void updateCartItemQty(Long cartItemId, int soLuong) {
        ChiTietGioHang item = chiTietGioHangRepo.findById(cartItemId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm trong giỏ!"));
        if (soLuong < 1) throw new RuntimeException("Số lượng phải >= 1");
        item.setSoLuong(soLuong);
        chiTietGioHangRepo.save(item);
    }
}
