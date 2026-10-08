package com.example.horecaone.service.contact;

import com.example.horecaone.dto.contact.CompanyInfoDto;
import com.example.horecaone.dto.contact.ContactDto;
import com.example.horecaone.dto.contact.ContactStatusRequest;
import java.util.List;

public interface ContactService {
    void createContact(ContactDto request);
    CompanyInfoDto getCompanyInfo();
    List<ContactDto> getAllContacts();
    ContactDto getContactById(Long id);
    void updateContactStatus(Long id, ContactStatusRequest request);
    void deleteContact(Long id);
    void updateCompanyInfo(CompanyInfoDto request);
}