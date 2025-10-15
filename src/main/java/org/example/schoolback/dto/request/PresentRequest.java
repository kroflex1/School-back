package org.example.schoolback.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class PresentRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotNull(message = "Price coins is required")
    @Min(value = 0, message = "Price cannot be negative")
    private Long priceCoins;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private Integer stock;
}