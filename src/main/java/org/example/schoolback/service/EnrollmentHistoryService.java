package org.example.schoolback.service;

import org.example.schoolback.entity.EnrollmentHistory;
import org.example.schoolback.entity.User;
import org.example.schoolback.repository.EnrollmentHistoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class EnrollmentHistoryService {

    @Autowired
    private EnrollmentHistoryRepository enrollmentHistoryRepository;

    public void saveEnrollmentHistory(User teacher, User student, Long coins) {
        EnrollmentHistory history = new EnrollmentHistory();
        history.setTeacher(teacher);
        history.setUser(student);
        history.setEnrolledCoins(coins);
        history.setEnrollmentDate(LocalDateTime.now());

        enrollmentHistoryRepository.save(history);
    }

    public List<EnrollmentHistory> getEnrollmentHistoryByUser(User user) {
        return enrollmentHistoryRepository.findByUser(user);
    }

    public List<EnrollmentHistory> getEnrollmentHistoryByTeacher(User teacher) {
        return enrollmentHistoryRepository.findByTeacher(teacher);
    }
}
