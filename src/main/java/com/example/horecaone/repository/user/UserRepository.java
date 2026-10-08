package com.example.horecaone.repository.user;

import com.example.horecaone.entity.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    boolean existsByEmail(String email);


    @Modifying
    @Query("UPDATE User u SET u.hoTen = :hoTen, u.soDienThoai = :soDienThoai, u.ngaySua = :ngaySua WHERE u.id = :id")
    int updateProfile(@Param("id") Long id,
                      @Param("hoTen") String hoTen,
                      @Param("soDienThoai") String soDienThoai,
                      @Param("ngaySua") LocalDateTime ngaySua);

    @Modifying(clearAutomatically = true, flushAutomatically = true) // ← thêm
    @Query("UPDATE User u SET u.matKhauHash = :hash, u.ngaySua = :ngaySua WHERE u.id = :id")
    int updatePassword(@Param("id") Long id,
                       @Param("hash") String hash,
                       @Param("ngaySua") LocalDateTime ngaySua);

    @Modifying
    @Query("UPDATE User u SET u.anhDaiDien = :anhDaiDien, u.ngaySua = :ngaySua WHERE u.id = :id")
    int updateAvatar(@Param("id") Long id,
                     @Param("anhDaiDien") String anhDaiDien,
                     @Param("ngaySua") LocalDateTime ngaySua);
}