package org.example.schoolback.repository;

import org.example.schoolback.entity.PhotoOfPresent;
import org.example.schoolback.entity.Present;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PhotoOfPresentRepository extends JpaRepository<PhotoOfPresent, Long> {

    List<PhotoOfPresent> findByPresent(Present present);
}
