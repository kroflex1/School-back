package org.example.schoolback.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "presents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Present {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "price_coins", nullable = false)
    private Long priceCoins;

    @Column(name = "stock", nullable = false)
    private Integer stock;

    // ОДИН подарок -> МНОГО фото
    @OneToMany(mappedBy = "present", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PhotoOfPresent> photos = new ArrayList<>();

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    public void removePhoto(PhotoOfPresent photo) {
        this.photos.remove(photo);
        photo.setPresent(null); // разрываем обратную связь
    }

    // Хелпер-метод для добавления фото
    public void addPhoto(PhotoOfPresent photo) {
        photos.add(photo);
        photo.setPresent(this);
    }
}