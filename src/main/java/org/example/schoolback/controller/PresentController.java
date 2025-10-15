package org.example.schoolback.controller;

import lombok.RequiredArgsConstructor;
import org.example.schoolback.entity.Present;
import org.example.schoolback.repository.PresentRepository;
import org.example.schoolback.service.PresentService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/presents")
@RequiredArgsConstructor
public class PresentController {

    private final PresentService presentService;
    private final PresentRepository presentRepository;

    // CREATE - создание подарка с несколькими фото
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Present> createPresent(
            @RequestParam("name") String name,
            @RequestParam("priceCoins") Long priceCoins,
            @RequestParam("stock") Integer stock,
            @RequestParam(value = "photos", required = false) List<MultipartFile> photos) {

        Present present = new Present();
        present.setName(name);
        present.setPriceCoins(priceCoins);
        present.setStock(stock);

        Present savedPresent = presentService.createPresent(present, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedPresent);
    }

    // ADD - добавление фото к существующему подарку
    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Present> addPhotos(
            @PathVariable Long id,
            @RequestParam("photos") List<MultipartFile> photos) {

        Present updatedPresent = presentService.addPhotos(id, photos);
        return ResponseEntity.ok(updatedPresent);
    }

    // DELETE - удаление конкретного фото
    @DeleteMapping("/{presentId}/photos/{photoId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletePhoto(
            @PathVariable Long presentId,
            @PathVariable Long photoId) {

        presentService.deletePhoto(presentId, photoId);
        return ResponseEntity.noContent().build();
    }

//    // PATCH запрос для частичного обновления
//    @PatchMapping("/{id}")
//    @PreAuthorize("hasRole('ADMIN')")
//    public ResponseEntity<Present> partialUpdatePresent(
//            @PathVariable Long id,
//            @RequestBody PresentUpdateRequest updateRequest) {
//
//        Present updatedPresent = presentService.updatePresent(id, updateRequest);
//        updatedPresent.setPhotos(null); // очищаем фото в ответе
//        return ResponseEntity.ok(updatedPresent);
//    }
}
