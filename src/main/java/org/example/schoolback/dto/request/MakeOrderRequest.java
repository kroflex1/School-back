package org.example.schoolback.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class MakeOrderRequest {
    @JsonProperty("customer_id")
    private Long customerId;

    @JsonProperty(value = "present_id")
    private Long presentId;
}
