package org.example.schoolback.util.assembler.impl;

import org.example.schoolback.dto.EnrollmentHistoryDTO;
import org.example.schoolback.entity.EnrollmentHistory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EnrollmentHistoryAssembler {

    public EnrollmentHistoryDTO toDto(EnrollmentHistory enrollmentHistory) {
        EnrollmentHistoryDTO dto = new EnrollmentHistoryDTO();
        dto.setId(enrollmentHistory.getId());
        dto.setTeacherName(enrollmentHistory.getTeacher().getFirstName() + " " +
                enrollmentHistory.getTeacher().getPatronymicName());
        dto.setStudentName(enrollmentHistory.getUser().getFirstName() + " " +
                enrollmentHistory.getUser().getPatronymicName());
        dto.setEnrolledCoins(enrollmentHistory.getEnrolledCoins());
        dto.setDate(enrollmentHistory.getEnrollmentDate());
        return dto;
    }

    public List<EnrollmentHistoryDTO> toDtoList(List<EnrollmentHistory> enrollmentHistories) {
        return enrollmentHistories.stream()
                .map(this::toDto)
                .collect(Collectors.toList());
    }
}
