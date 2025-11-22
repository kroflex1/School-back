package org.example.schoolback.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresentDTO {
    @JsonProperty(value = "id", access = JsonProperty.Access.READ_ONLY)
    private Long id;

    @JsonProperty(value = "name")
    private String name;

    @JsonProperty(value = "price")
    private Long priceCoins;

    @JsonProperty(value = "stock")
    private Integer stock;

    @JsonProperty(value = "photo_ids")
    private List<Long> photoIds;
}
