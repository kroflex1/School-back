package org.example.schoolback.repository;

import org.example.schoolback.entity.EnrollmentHistory;
import org.example.schoolback.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EnrollmentHistoryRepository extends JpaRepository<EnrollmentHistory, Long> {

    Page<EnrollmentHistory> findByUser(User user, Pageable pageable);

    Page<EnrollmentHistory> findByTeacher(User teacher);
}