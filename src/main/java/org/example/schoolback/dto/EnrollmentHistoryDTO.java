package org.example.schoolback.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EnrollmentHistoryDTO {

    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty("teacher_name")
    private String teacherName;

    @JsonProperty("student_name")
    private String studentName;

    @JsonProperty("enrolled_coins")
    private Long enrolledCoins;

    @JsonProperty(value = "date")
    private LocalDateTime date;
}