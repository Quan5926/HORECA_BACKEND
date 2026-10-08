package com.example.horecaone.repository.product;

import com.example.horecaone.entity.product.ChiTietCombo;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ChiTietComboRepository extends JpaRepository<ChiTietCombo, Long> {

    List<ChiTietCombo> findByChiTietComboId(Long chiTietComboId);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM CHI_TIET_COMBO WHERE CHI_TIET_COMBO_ID = :id", nativeQuery = true)
    void deleteByChiTietComboId(@Param("id") Long id);

    @Modifying
    @Transactional
    @Query(value = "DELETE FROM CHI_TIET_COMBO WHERE CHI_TIET_COMBO_ID IN :ids", nativeQuery = true)
    void deleteByChiTietComboIdIn(@Param("ids") List<Long> ids);
}