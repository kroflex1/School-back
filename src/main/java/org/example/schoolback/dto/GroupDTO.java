package org.example.schoolback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class GroupDTO {

    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty(value = "teacher", access = JsonProperty.Access.READ_ONLY)
    private UserDTO teacher;

    @JsonProperty("teacher_id")
    private Long teacherId;

    @JsonProperty(value = "participants", access = JsonProperty.Access.READ_ONLY)
    private List<UserDTO> participants;
}
