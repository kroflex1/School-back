package org.example.schoolback.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class CreateGroupRequest {
    @JsonProperty("group_name")
    private String groupName;

    @JsonProperty(value = "teacher_id")
    private Long teacherId;
}
