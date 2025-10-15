package org.example.schoolback.repository;

import org.example.schoolback.entity.Present;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresentRepository extends JpaRepository<Present, Long> {

}