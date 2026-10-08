package com.example.horecaone.service.contact;

import com.example.horecaone.dto.contact.CompanyInfoDto;
import com.example.horecaone.dto.contact.ContactDto;
import com.example.horecaone.dto.contact.ContactStatusRequest;
import com.example.horecaone.entity.contact.LienHe;
import com.example.horecaone.entity.contact.ThongTinCongTy;
import com.example.horecaone.repository.contact.LienHeRepository;
import com.example.horecaone.repository.contact.ThongTinCongTyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ContactServiceImpl implements ContactService {

    @Autowired
    private LienHeRepository lienHeRepository;

    @Autowired
    private ThongTinCongTyRepository thongTinCongTyRepo;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void createContact(ContactDto request) {
        LienHe entity = new LienHe();
        entity.setHoTen(request.getHoTen());
        entity.setEmail(request.getEmail());
        entity.setSoDienThoai(request.getSoDienThoai());
        entity.setTieuDe(request.getTieuDe());
        entity.setNoiDung(request.getNoiDung());
        entity.setTrangThai("CHUA_DOC");
        lienHeRepository.save(entity);
    }

    @Override
    public CompanyInfoDto getCompanyInfo() {
        ThongTinCongTy entity = thongTinCongTyRepo.findById(1L).orElse(new ThongTinCongTy());

        CompanyInfoDto dto = new CompanyInfoDto();
        dto.setName(entity.getTenCongTy());
        dto.setPhone(entity.getSoDienThoai());
        dto.setEmail(entity.getEmail());
        dto.setOffice(entity.getVanPhong());
        dto.setFactory(entity.getNhaMay());
        dto.setMapUrl(entity.getMapUrl());
        return dto;
    }

    @Override
    public List<ContactDto> getAllContacts() {
        List<LienHe> list = lienHeRepository.findAll(Sort.by(Sort.Direction.DESC, "ngayTao"));
        return list.stream().map(this::mapToContactDto).collect(Collectors.toList());
    }

    @Override
    public ContactDto getContactById(Long id) {
        LienHe entity = lienHeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy liên hệ"));
        return mapToContactDto(entity);
    }

    @Override
    public void updateContactStatus(Long id, ContactStatusRequest request) {
        LienHe entity = lienHeRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Không tìm thấy liên hệ"));

        if (request.getTrangThai() != null) entity.setTrangThai(request.getTrangThai());
        if (request.getGhiChuAdmin() != null) entity.setGhiChuAdmin(request.getGhiChuAdmin());
        if (request.getNguoiSua() != null) entity.setNguoiSua(request.getNguoiSua());

        lienHeRepository.save(entity);
    }

    @Override
    public void deleteContact(Long id) {
        if (!lienHeRepository.existsById(id)) {
            throw new RuntimeException("Không tìm thấy liên hệ");
        }
        lienHeRepository.deleteById(id);
    }

    @Override
    public void updateCompanyInfo(CompanyInfoDto request) {
        ThongTinCongTy entity = thongTinCongTyRepo.findById(1L).orElse(new ThongTinCongTy());
        entity.setId(1L);
        entity.setTenCongTy(request.getName());
        entity.setSoDienThoai(request.getPhone());
        entity.setEmail(request.getEmail());
        entity.setVanPhong(request.getOffice());
        entity.setNhaMay(request.getFactory());
        entity.setMapUrl(request.getMapUrl());

        if (request.getNguoiSua() != null) {
            entity.setNguoiSua(request.getNguoiSua());
        }

        thongTinCongTyRepo.save(entity);
    }

    private ContactDto mapToContactDto(LienHe entity) {
        ContactDto dto = new ContactDto();
        dto.setId(entity.getId());
        dto.setHoTen(entity.getHoTen());
        dto.setEmail(entity.getEmail());
        dto.setSoDienThoai(entity.getSoDienThoai());
        dto.setTieuDe(entity.getTieuDe());
        dto.setNoiDung(entity.getNoiDung());
        dto.setTrangThai(entity.getTrangThai());
        dto.setGhiChuAdmin(entity.getGhiChuAdmin());
        if (entity.getNgayTao() != null) {
            dto.setNgayTao(entity.getNgayTao().format(formatter));
        }
        return dto;
    }
}