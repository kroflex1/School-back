package org.example.schoolback.dto.response;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
public class AdminPresentResponse {
    private Long id;
    private String name;
    private Long priceCoins;
    private Integer stock;
    private List<PhotoResponse> photos;
}