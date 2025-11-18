package org.example.schoolback.repository;

import org.example.schoolback.entity.EnrollmentHistory;
import org.example.schoolback.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentHistoryRepository extends JpaRepository<EnrollmentHistory, Long> {

    List<EnrollmentHistory> findByUser(User user);

    List<EnrollmentHistory> findByTeacherOrderByEnrollmentDateDesc(User teacher);
}