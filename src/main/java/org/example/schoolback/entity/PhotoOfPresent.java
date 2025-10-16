package org.example.schoolback.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "photos_presents")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class    PhotoOfPresent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Lob
    @Column(name = "photo_data", nullable = false, columnDefinition = "LONGBLOB")
    private byte[] photo;

    // МНОГО фото -> ОДИН подарок
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "present_id", nullable = false)
    private Present present;
}
