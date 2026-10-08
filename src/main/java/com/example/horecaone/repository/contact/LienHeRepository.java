package com.example.horecaone.repository.contact;

import com.example.horecaone.entity.contact.LienHe;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface LienHeRepository extends JpaRepository<LienHe, Long> {
}