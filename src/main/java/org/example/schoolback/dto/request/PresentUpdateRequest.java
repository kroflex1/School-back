package org.example.schoolback.dto.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PresentUpdateRequest {
    private String name;

    @Min(value = 0, message = "Price cannot be negative")
    private Long priceCoins;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}