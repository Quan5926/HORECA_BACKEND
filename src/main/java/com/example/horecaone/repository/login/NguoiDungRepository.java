package com.example.horecaone.repository.login;

import com.example.horecaone.entity.login.NguoiDung;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository  // ← BẮT BUỘC phải có dòng này
public interface NguoiDungRepository extends JpaRepository<NguoiDung, Long> {

    Optional<NguoiDung> findByEmail(String email);

    Optional<NguoiDung> findBySoDienThoai(String soDienThoai);

    default Optional<NguoiDung> findByEmailOrSoDienThoai(String identifier) {
        return findByEmail(identifier)
                .or(() -> findBySoDienThoai(identifier));
    }


}