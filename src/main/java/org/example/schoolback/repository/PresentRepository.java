package org.example.schoolback.repository;

import org.example.schoolback.entity.Present;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PresentRepository extends JpaRepository<Present, Long> {

    Page<Present> findByStockGreaterThan(Integer stock, Pageable pageable);

    @Query("SELECT p FROM Present p WHERE LOWER(p.name) LIKE LOWER(CONCAT('%', :query, '%')) AND p.stock > 0")
    List<Present> findByNameContainingIgnoreCaseAndStockGreaterThan(@Param("query") String query);

    List<Present> findByStockGreaterThan(Integer stock);
}