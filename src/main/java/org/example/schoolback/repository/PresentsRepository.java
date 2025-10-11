package org.example.schoolback.repository;

import org.example.schoolback.entity.Presents;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PresentsRepository extends JpaRepository<Presents, Long> {

}