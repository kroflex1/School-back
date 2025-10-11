package org.example.schoolback.repository;

import org.example.schoolback.entity.PhotoOfPresent;
import org.example.schoolback.entity.Presents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoOfPresentRepository extends JpaRepository<PhotoOfPresent, Long> {

    List<PhotoOfPresent> findByPresent(Presents present);
}