package org.example.schoolback.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class PresentResponse {
    private Long id;
    private String name;
    private Long priceCoins;
    private Integer stock;
    private List<Long> photoIds;

    public PresentResponse(Long id, String name, Long priceCoins, Integer stock, String description) {
        this.id = id;
        this.name = name;
        this.priceCoins = priceCoins;
        this.stock = stock;
    }
}