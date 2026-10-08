package com.example.horecaone.repository.contact;

import com.example.horecaone.entity.contact.ThongTinCongTy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ThongTinCongTyRepository extends JpaRepository<ThongTinCongTy, Long> {
}