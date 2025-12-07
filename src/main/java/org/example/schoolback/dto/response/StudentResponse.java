package org.example.schoolback.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class StudentResponse {
    private Long id;
    private String fullName;
    private String login;
    private String birthDate;
    private String coins;
}