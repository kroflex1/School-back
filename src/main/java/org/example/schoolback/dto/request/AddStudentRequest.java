package org.example.schoolback.dto.request;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class AddStudentRequest {

    @JsonProperty(value = "student_id")
    private Long studentId;
}
