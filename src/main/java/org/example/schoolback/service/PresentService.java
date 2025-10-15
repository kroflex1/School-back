package org.example.schoolback.service;

import org.example.schoolback.entity.PhotoOfPresent;
import org.example.schoolback.entity.Present;
import org.example.schoolback.repository.PhotoOfPresentRepository;
import org.example.schoolback.repository.PresentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class PresentService {

    private final PresentRepository presentRepository;
    private final PhotoOfPresentRepository photoOfPresentRepository;

    // CREATE - создание подарка с несколькими фото
    public Present createPresent(Present present, List<MultipartFile> photoFiles) {
        // Валидация
        if (present.getName().trim().isEmpty()) {
            throw new RuntimeException("Проверьте название подарка");
        }
        if (present.getStock() == null || present.getStock() < 1) {
            throw new RuntimeException("Проверьте количество доступных подарков");
        }
        if (present.getPriceCoins() == null || present.getPriceCoins() < 0) {
            throw new RuntimeException("Проверьте стоимость подарка");
        }

            // Сохраняем подарок
        Present savedPresent = presentRepository.save(present);

            // Сохраняем фото если они есть
        if (!photoFiles.isEmpty()) {
            for (MultipartFile photoFile : photoFiles) {
                if (photoFile != null && !photoFile.isEmpty()) {
                    try {
                        PhotoOfPresent photo = new PhotoOfPresent();
                        photo.setPhoto(photoFile.getBytes());
                        photo.setPresent(savedPresent);
                        photoOfPresentRepository.save(photo);
                    } catch (IOException e) {
                        throw new RuntimeException("Не удалось сохранить фотографию", e);
                    }
                }
            }
        }
        return savedPresent;
    }

    // READ - получение подарка с фото
    @Transactional(readOnly = true)
    public Present getPresentWithPhotos(Long id) {
        return presentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Подарок не найден"));
    }

    // ADD - добавление фото к существующему подарку
    public Present addPhotos(Long presentId, List<MultipartFile> photoFiles) {
        Present present = getPresentWithPhotos(presentId);

        if (photoFiles != null && !photoFiles.isEmpty()) {
            int startOrder = present.getPhotos().size();

            for (MultipartFile photoFile : photoFiles) {
                if (photoFile != null && !photoFile.isEmpty()) {
                    try {
                        PhotoOfPresent photo = new PhotoOfPresent();
                        photo.setPhoto(photoFile.getBytes());
                        photo.setPresent(present);
                        photoOfPresentRepository.save(photo);
                    } catch (IOException e) {
                        throw new RuntimeException("Failed to process photo", e);
                    }
                }
            }
        }

        return present;
    }

    // DELETE - удаление конкретного фото
    public void deletePhoto(Long presentId, Long photoId) {
        Present present = presentRepository.findById(presentId)
                .orElseThrow(() -> new RuntimeException("Present not found"));

        PhotoOfPresent photoToRemove = present.getPhotos().stream()
                .filter(photo -> photo.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        // Используем хелпер-метод
        present.removePhoto(photoToRemove);

        presentRepository.save(present);
    }

    // UPDATE - обновление подарка (без изменения фото)
    public Present updatePresentWithoutPhoto(Long id, Present presentDetails) {
        Present present = getPresentWithPhotos(id);

        // Обновляем только те поля, которые не null
        if (presentDetails.getName() != null) {
            present.setName(presentDetails.getName());
        }
        if (presentDetails.getPriceCoins() != null) {
            present.setPriceCoins(presentDetails.getPriceCoins());
        }
        if (presentDetails.getStock() != null) {
            present.setStock(presentDetails.getStock());
        }

        return presentRepository.save(present);
    }

    // DELETE - удаление подарка (все фото удалятся каскадно)
    public void deletePresent(Long id) {
        Present present = getPresentWithPhotos(id);
        presentRepository.delete(present);
    }
}