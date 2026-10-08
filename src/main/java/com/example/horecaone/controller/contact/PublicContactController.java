package com.example.horecaone.controller.contact;

import com.example.horecaone.dto.contact.CompanyInfoDto;
import com.example.horecaone.dto.contact.ContactDto;
import com.example.horecaone.service.contact.ContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PublicContactController {

    @Autowired
    private ContactService contactService;

    @PostMapping("/lien-he")
    public ResponseEntity<?> sendContact(@RequestBody ContactDto request) {
        contactService.createContact(request);
        return ResponseEntity.ok().body("{\"message\": \"Gửi liên hệ thành công\"}");
    }

    @GetMapping("/thong-tin-cong-ty")
    public ResponseEntity<CompanyInfoDto> getCompanyInfo() {
        return ResponseEntity.ok(contactService.getCompanyInfo());
    }
}