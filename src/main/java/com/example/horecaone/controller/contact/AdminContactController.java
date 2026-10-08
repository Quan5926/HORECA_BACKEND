package com.example.horecaone.controller.contact;

import com.example.horecaone.dto.contact.CompanyInfoDto;
import com.example.horecaone.dto.contact.ContactDto;
import com.example.horecaone.dto.contact.ContactStatusRequest;
import com.example.horecaone.service.contact.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
public class AdminContactController {

    @Autowired
    private ContactService contactService;

    @GetMapping("/lien-he")
    public ResponseEntity<List<ContactDto>> getAllContacts() {
        return ResponseEntity.ok(contactService.getAllContacts());
    }

    @GetMapping("/lien-he/{id}")
    public ResponseEntity<ContactDto> getContactById(@PathVariable Long id) {
        return ResponseEntity.ok(contactService.getContactById(id));
    }

    @PatchMapping("/lien-he/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable Long id, @RequestBody ContactStatusRequest request) {
        contactService.updateContactStatus(id, request);
        return ResponseEntity.ok().body("{\"message\": \"Cập nhật trạng thái thành công\"}");
    }

    @DeleteMapping("/lien-he/{id}")
    public ResponseEntity<?> deleteContact(@PathVariable Long id) {
        contactService.deleteContact(id);
        return ResponseEntity.ok().body("{\"message\": \"Xóa thành công\"}");
    }

    @PutMapping("/thong-tin-cong-ty")
    public ResponseEntity<?> updateCompanyInfo(@RequestBody CompanyInfoDto request) {
        contactService.updateCompanyInfo(request);
        return ResponseEntity.ok().body("{\"message\": \"Cập nhật cấu hình công ty thành công\"}");
    }
}