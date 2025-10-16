package org.example.schoolback.repository;

import org.example.schoolback.entity.PhotoOfPresent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhotoOfPresentRepository extends JpaRepository<PhotoOfPresent, Long> {

    List<PhotoOfPresent> findByPresentId(Long presentId);

    void deleteByPresentId(Long presentId);

    Optional<PhotoOfPresent> findFirstByPresentId(Long presentId); // для главного фото
}