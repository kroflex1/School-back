package org.example.schoolback.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.example.schoolback.dto.request.PresentUpdateRequest;
import org.example.schoolback.dto.response.PresentResponse;
import org.example.schoolback.entity.PhotoOfPresent;
import org.example.schoolback.entity.Present;
import org.example.schoolback.service.PresentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/presents")
@RequiredArgsConstructor
public class PresentController {

    private final PresentService presentService;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Создать подарок", description = "Создание нового подарка с фотографиями")
    public ResponseEntity<PresentResponse> createPresent(
            @Parameter(description = "Название подарка") @RequestParam String name,
            @Parameter(description = "Цена в монетах") @RequestParam Long priceCoins,
            @Parameter(description = "Количество в наличии") @RequestParam Integer stock,
            @Parameter(description = "Фотографии подарка") @RequestParam(required = false) List<MultipartFile> photos) {

        PresentUpdateRequest request = new PresentUpdateRequest();
        request.setName(name);
        request.setPriceCoins(priceCoins);
        request.setStock(stock);

        Present present = presentService.createPresent(request, photos);
        return ResponseEntity.status(HttpStatus.CREATED).body(convertToPresentResponse(present));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
    @GetMapping("/{id}")
    @Operation(summary = "Получить подарок", description = "Получение информации о конкретном подарке")
    public ResponseEntity<PresentResponse> getPresent(
            @Parameter(description = "ID подарка") @PathVariable Long id) {
        Present present = presentService.getPresentWithPhotos(id);
        return ResponseEntity.ok(convertToPresentResponse(present));
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'TEACHER', 'STUDENT')")
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

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping
    @Operation(summary = "Получить список доступных подарков",
            description = """
                    Получить список подарков, доступные пользователю.
                    
                    **Влияние роли:**
                    - ADMIN: получает информацию о всех подарках, даже которых нет в наличии;
                    - STUDENT: получает информацию о подарках, которые есть в наличии;
                    """)
    public Page<PresentResponse> getPresents(
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Present> presents = presentService.getAvailablePresentsForCurrentUser(pageable);
        return presents.map(this::convertToPresentResponse);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT')")
    @GetMapping("/search")
    @Operation(summary = "Поиск подарков по названию",
            description = "Получить список подарков по названию (частичное совпадение, без учета регистра) только из тех, что есть в наличии (остаток > 0)")
    public Page<PresentResponse> searchPresents(
            @Parameter(description = "Название подарка") @RequestParam String query,
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortDir) {
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDir) ? Sort.Direction.ASC : Sort.Direction.DESC;
        Sort sort = Sort.by(direction, sortBy);
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<Present> presents = presentService.searchPresents(query, pageable);
        return presents.map(this::convertToPresentResponse);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    @Operation(summary = "Обновить инормацию о подарке", description = "Обновление данных подарка")
    public ResponseEntity<PresentResponse> updatePresent(
            @Parameter(description = "ID подарка") @PathVariable Long id,
            @RequestBody @Valid PresentUpdateRequest updateRequest) {
        Present present = presentService.updatePresent(id, updateRequest);
        return ResponseEntity.ok(convertToPresentResponse(present));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Добавить фотографии", description = "Добавление фотографий к существующему подарку")
    public ResponseEntity<PresentResponse> addPhotos(
            @Parameter(description = "ID подарка") @PathVariable Long id,
            @Parameter(description = "Фотографии для добавления") @RequestParam List<MultipartFile> photos) {
        Present present = presentService.addPhotos(id, photos);
        return ResponseEntity.ok(convertToPresentResponse(present));
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    @Operation(summary = "Удалить подарок", description = "Удаление подарка и всех его фотографий")
    public ResponseEntity<Void> deletePresent(
            @Parameter(description = "ID подарка") @PathVariable Long id) {
        presentService.deletePresent(id);
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{presentId}/photos/{photoId}")
    @Operation(summary = "Удалить фотографию", description = "Удаление конкретной фотографии подарка")
    public ResponseEntity<Void> deletePhoto(
            @Parameter(description = "ID подарка") @PathVariable Long presentId,
            @Parameter(description = "ID фотографии") @PathVariable Long photoId) {
        presentService.deletePhoto(presentId, photoId);
        return ResponseEntity.noContent().build();
    }
    
    private PresentResponse convertToPresentResponse(Present present) {
        PresentResponse response = new PresentResponse();
        response.setId(present.getId());
        response.setName(present.getName());
        response.setPriceCoins(present.getPriceCoins());
        response.setStock(present.getStock());

        // Добавляем ID всех фото подарка
        if (present.getPhotos() != null) {
            List<Long> photoIds = present.getPhotos().stream()
                    .map(PhotoOfPresent::getId)
                    .collect(Collectors.toList());
            response.setPhotoIds(photoIds);
        }

        return response;
    }
}
