package com.example.horecaone.service.address;
import com.example.horecaone.dto.address.AddressRequestDTO;
import com.example.horecaone.entity.address.DiaChiGiaoHang;
import com.example.horecaone.repository.address.DiaChiGiaoHangRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class AddressService {

    @Autowired
    private DiaChiGiaoHangRepository addressRepo;

    // --- 1. THÊM ĐỊA CHỈ MỚI ---
    @Transactional
    public DiaChiGiaoHang createAddress(Long userId, AddressRequestDTO dto) {
        // Nếu user tick chọn "Mặc định", phải set các địa chỉ cũ về false
        if (Boolean.TRUE.equals(dto.getLaMacDinh())) {
            addressRepo.resetDefaultAddressForUser(userId);
        } else {
            // Nếu user chưa có địa chỉ nào, bắt buộc địa chỉ đầu tiên phải là mặc định
            List<DiaChiGiaoHang> existing = addressRepo.findByNguoiDungId(userId);
            if (existing.isEmpty()) {
                dto.setLaMacDinh(true);
            }
        }

        DiaChiGiaoHang entity = new DiaChiGiaoHang();
        entity.setNguoiDungId(userId);
        entity.setHoTenNguoiNhan(dto.getTenNguoiNhan());
        entity.setSoDienThoai(dto.getSdt());

        // Ghép 4 trường của FE thành 1 chuỗi hoàn chỉnh lưu vào DB
        String fullAddress = String.format("%s, %s, %s, %s",
                dto.getDiaChiChiTiet(), dto.getPhuongXa(), dto.getQuanHuyen(), dto.getTinhThanh());
        entity.setDiaChiDayDu(fullAddress);

        entity.setLaDiaChiMacDinh(dto.getLaMacDinh() != null ? dto.getLaMacDinh() : false);

        // Audit fields
        entity.setNguoiTao(userId);
        entity.setNgayTao(LocalDateTime.now());

        return addressRepo.save(entity);
    }

    // --- 2. CẬP NHẬT ĐỊA CHỈ ---
    @Transactional
    public DiaChiGiaoHang updateAddress(Long userId, Long addressId, AddressRequestDTO dto) {
        DiaChiGiaoHang entity = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ!"));

        // Bảo mật: Kiểm tra xem địa chỉ này có đúng của User đang đăng nhập không
        if (!entity.getNguoiDungId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền sửa địa chỉ này!");
        }

        // Cập nhật logic mặc định
        if (Boolean.TRUE.equals(dto.getLaMacDinh()) && !entity.getLaDiaChiMacDinh()) {
            addressRepo.resetDefaultAddressForUser(userId);
            entity.setLaDiaChiMacDinh(true);
        } else if (Boolean.FALSE.equals(dto.getLaMacDinh())) {
            entity.setLaDiaChiMacDinh(false);
        }

        entity.setHoTenNguoiNhan(dto.getTenNguoiNhan());
        entity.setSoDienThoai(dto.getSdt());

        // Ghép chuỗi mới
        String fullAddress = String.format("%s, %s, %s, %s",
                dto.getDiaChiChiTiet(), dto.getPhuongXa(), dto.getQuanHuyen(), dto.getTinhThanh());
        entity.setDiaChiDayDu(fullAddress);

        // Audit fields
        entity.setNguoiSua(userId);
        entity.setNgaySua(LocalDateTime.now());

        return addressRepo.save(entity);
    }

    // --- 3. LẤY DANH SÁCH ĐỊA CHỈ THEO USER ---
    public List<DiaChiGiaoHang> getUserAddresses(Long userId) {
        return addressRepo.findByNguoiDungId(userId);
    }

    // --- 4. XÓA ĐỊA CHỈ ---
    public void deleteAddress(Long userId, Long addressId) {
        DiaChiGiaoHang existing = addressRepo.findById(addressId)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy địa chỉ"));

        if (!existing.getNguoiDungId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền xóa địa chỉ này");
        }

        try {
            addressRepo.delete(existing); // Cố gắng xóa
        } catch (DataIntegrityViolationException e) {
            // Bắt quả tang lỗi dính khóa ngoại (đã có đơn hàng)
            throw new RuntimeException("Không thể xóa: Địa chỉ này đã được sử dụng trong lịch sử đơn hàng!");
        }
    }
}
