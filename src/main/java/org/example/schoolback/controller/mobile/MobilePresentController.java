package org.example.schoolback.controller.mobile;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.example.schoolback.dto.response.MobilePresentResponse;
import org.example.schoolback.dto.response.PhotoResponse;
import org.example.schoolback.entity.Present;
import org.example.schoolback.entity.PhotoOfPresent;
import org.example.schoolback.service.PresentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/mobile/presents")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
public class MobilePresentController {

    private final PresentService presentService;

//    Чтобы получать не все подарки сразу, а частями, уменьшение нагрузки на приложение
    @GetMapping
    @Operation(summary = "Получить подарки", description = "Получение списка подарков с пагинацией")
    public ResponseEntity<List<MobilePresentResponse>> getPresents(
            @Parameter(description = "Номер страницы") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Размер страницы") @RequestParam(defaultValue = "20") int size) {

        Page<Present> presents = presentService.getAvailablePresents(PageRequest.of(page, size));
        List<MobilePresentResponse> responses = presents.stream()
                .map(this::convertToMobileResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{id}")
    @Operation(summary = "Получить информацию о подарке", description = "Получение детальной информации о конкретном подарке")
    public ResponseEntity<MobilePresentResponse> getPresent(
            @Parameter(description = "ID подарка") @PathVariable Long id) {

        Present present = presentService.getPresentWithPhotos(id);
        return ResponseEntity.ok(convertToMobileResponse(present));
    }

    @GetMapping("/search")
    @Operation(summary = "Поиск подарков", description = "Поиск подарков по названию")
    public ResponseEntity<List<MobilePresentResponse>> searchPresents(
            @Parameter(description = "Поисковый запрос") @RequestParam String query) {

        List<Present> presents = presentService.searchPresents(query);
        List<MobilePresentResponse> responses = presents.stream()
                .map(this::convertToMobileResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(responses);
    }

    @GetMapping("/{presentId}/photos/{photoId}")
    @Operation(summary = "Получить фотографию", description = "Получение фотографии подарка")
    public ResponseEntity<byte[]> getPhoto(
            @Parameter(description = "ID подарка") @PathVariable Long presentId,
            @Parameter(description = "ID фотографии") @PathVariable Long photoId) {

        byte[] photoData = presentService.getPhotoData(presentId, photoId);
        return ResponseEntity.ok()
                .contentType(MediaType.IMAGE_JPEG)
                .body(photoData);
    }

    private MobilePresentResponse convertToMobileResponse(Present present) {
        MobilePresentResponse response = new MobilePresentResponse();
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