package org.example.schoolback.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "presents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Presents {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "prise_coins", nullable = false)
    private Long priseCoins;

    @Column(name = "stock", nullable = false)
    private Integer stock;
}