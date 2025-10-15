package org.example.schoolback.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PresentUpdateRequest {

    @Size(min = 1, max = 100, message = "Name must be between 1-100 characters")
    private String name;

    @Min(value = 0, message = "Price cannot be negative")
    private Long priceCoins;

    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}