package org.example.schoolback.controller.admin;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.schoolback.dto.request.PresentUpdateRequest;
import org.example.schoolback.dto.response.AdminPresentResponse;
import org.example.schoolback.dto.response.PhotoResponse;
import org.example.schoolback.entity.Present;
import org.example.schoolback.entity.PhotoOfPresent;
import org.example.schoolback.service.PresentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/admin/presents")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class AdminPresentController {

    private final PresentService presentService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать подарок", description = "Создание нового подарка с фотографиями")
    public ResponseEntity<AdminPresentResponse> createPresent(
            @Parameter(description = "Название подарка") @RequestParam String name,
            @Parameter(description = "Цена в монетах") @RequestParam Long priceCoins,
            @Parameter(description = "Количество в наличии") @RequestParam Integer stock,
            @Parameter(description = "Фотографии подарка") @RequestParam(required = false) List<MultipartFile> photos) {

        PresentUpdateRequest request = new PresentUpdateRequest();
        request.setName(name);
        request.setPriceCoins(priceCoins);
        request.setStock(stock);

        Present present = presentService.createPresent(request, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToAdminResponse(present));
    }

    @GetMapping
    @Operation(summary = "Получить все подарки", description = "Получение списка всех подарков")
    public ResponseEntity<List<AdminPresentResponse>> getAllPresents() {
        List<Present> presents = presentService.getAllPresents();
        List<AdminPresentResponse> responses = presents.stream()
                .map(this::convertToAdminResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить подарок", description = "Получение информации о конкретном подарке")
    public ResponseEntity<AdminPresentResponse> getPresent(
            @Parameter(description = "ID подарка") @PathVariable Long id) {
        Present present = presentService.getPresentWithPhotos(id);
        return ResponseEntity.ok(convertToAdminResponse(present));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Обновить подарок", description = "Обновление данных подарка")
    public ResponseEntity<AdminPresentResponse> updatePresent(
            @Parameter(description = "ID подарка") @PathVariable Long id,
            @RequestBody @Valid PresentUpdateRequest updateRequest) {
        Present present = presentService.updatePresent(id, updateRequest);
        return ResponseEntity.ok(convertToAdminResponse(present));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить подарок", description = "Удаление подарка и всех его фотографий")
    public ResponseEntity<Void> deletePresent(
            @Parameter(description = "ID подарка") @PathVariable Long id) {
        presentService.deletePresent(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавить фотографии", description = "Добавление фотографий к существующему подарку")
    public ResponseEntity<AdminPresentResponse> addPhotos(
            @Parameter(description = "ID подарка") @PathVariable Long id,
            @Parameter(description = "Фотографии для добавления") @RequestParam List<MultipartFile> photos) {
        Present present = presentService.addPhotos(id, photos);
        return ResponseEntity.ok(convertToAdminResponse(present));
    }

    @DeleteMapping("/{presentId}/photos/{photoId}")
    @Operation(summary = "Удалить фотографию", description = "Удаление конкретной фотографии подарка")
    public ResponseEntity<Void> deletePhoto(
            @Parameter(description = "ID подарка") @PathVariable Long presentId,
            @Parameter(description = "ID фотографии") @PathVariable Long photoId) {
        presentService.deletePhoto(presentId, photoId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/{presentId}/photos/{photoId}")
    @Operation(summary = "Получить фотографию", description = "Получение файла фотографии")
    public ResponseEntity<byte[]> getPhoto(
            @Parameter(description = "ID подарка") @PathVariable Long presentId,
            @Parameter(description = "ID фотографии") @PathVariable Long photoId) {
        byte[] photoData = presentService.getPhotoData(presentId, photoId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photoData);
    }

    private AdminPresentResponse convertToAdminResponse(Present present) {
        AdminPresentResponse response = new AdminPresentResponse();
        response.setId(present.getId());
        response.setName(present.getName());
        response.setPriceCoins(present.getPriceCoins());
        response.setStock(present.getStock());

        if (present.getPhotos() != null) {
            List<PhotoResponse> photoResponses = present.getPhotos().stream()
                    .map(this::convertToPhotoResponse)
                    .collect(Collectors.toList());
            response.setPhotos(photoResponses);
        }

        return response;
    }

    private PhotoResponse convertToPhotoResponse(PhotoOfPresent photo) {
        PhotoResponse response = new PhotoResponse();
        response.setId(photo.getId());
        return response;
    }
}