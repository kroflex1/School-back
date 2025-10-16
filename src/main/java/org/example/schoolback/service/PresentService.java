package org.example.schoolback.service;

import org.example.schoolback.dto.request.PresentUpdateRequest;
import org.example.schoolback.entity.Present;
import org.example.schoolback.entity.PhotoOfPresent;
import org.example.schoolback.repository.PresentRepository;
import org.example.schoolback.repository.PhotoOfPresentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

    public Present createPresent(PresentUpdateRequest request, List<MultipartFile> photos) {
        validateCreateRequest(request);

        Present present = new Present();
        present.setName(request.getName());
        present.setPriceCoins(request.getPriceCoins());
        present.setStock(request.getStock());

        Present savedPresent = presentRepository.save(present);

        if (photos != null && !photos.isEmpty()) {
            addPhotosToPresent(savedPresent, photos);
        }

        return savedPresent;
    }

    // READ - For Admin
    @Transactional(readOnly = true)
    public List<Present> getAllPresents() {
        return presentRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Present getPresentById(Long id) {
        return presentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Present not found with id: " + id));
    }

    @Transactional(readOnly = true)
    public Present getPresentWithPhotos(Long id) {
        return presentRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Present not found with id: " + id));
    }

    // READ - For Mobile
    @Transactional(readOnly = true)
    public Page<Present> getAvailablePresents(Pageable pageable) {
        return presentRepository.findByStockGreaterThan(0, pageable);
    }

    @Transactional(readOnly = true)
    public List<Present> searchPresents(String query) {
        return presentRepository.findByNameContainingIgnoreCaseAndStockGreaterThan(query);
    }

    @Transactional(readOnly = true)
    public List<Present> getAvailablePresents() {
        return presentRepository.findByStockGreaterThan(0);
    }

    // UPDATE
    public Present updatePresent(Long id, PresentUpdateRequest updateRequest) {
        Present present = getPresentWithPhotos(id);

        if (updateRequest.getName() != null) {
            present.setName(updateRequest.getName());
        }
        if (updateRequest.getPriceCoins() != null) {
            present.setPriceCoins(updateRequest.getPriceCoins());
        }
        if (updateRequest.getStock() != null) {
            present.setStock(updateRequest.getStock());
        }

        return presentRepository.save(present);
    }

    // ADD PHOTOS to existing present
    public Present addPhotos(Long presentId, List<MultipartFile> photoFiles) {
        Present present = getPresentWithPhotos(presentId);

        if (photoFiles != null && !photoFiles.isEmpty()) {
            addPhotosToPresent(present, photoFiles);
        }

        return present;
    }

    // DELETE
    public void deletePresent(Long id) {
        Present present = getPresentWithPhotos(id);
        presentRepository.delete(present);
    }

    public void deletePhoto(Long presentId, Long photoId) {
        Present present = getPresentWithPhotos(presentId);

        PhotoOfPresent photoToRemove = present.getPhotos().stream()
                .filter(photo -> photo.getId().equals(photoId))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Photo not found with id: " + photoId));

        present.removePhoto(photoToRemove);
        presentRepository.save(present);
    }

    // PHOTO METHODS
    @Transactional(readOnly = true)
    public byte[] getPhotoData(Long presentId, Long photoId) {
        PhotoOfPresent photo = photoOfPresentRepository.findById(photoId)
                .orElseThrow(() -> new RuntimeException("Photo not found"));

        if (!photo.getPresent().getId().equals(presentId)) {
            throw new RuntimeException("Photo does not belong to this present");
        }

        return photo.getPhoto();
    }

    @Transactional(readOnly = true)
    public byte[] getMainPhotoData(Long presentId) {
        List<PhotoOfPresent> photos = photoOfPresentRepository.findByPresentId(presentId);
        if (photos.isEmpty()) {
            throw new RuntimeException("No photos found for present");
        }
        return photos.getFirst().getPhoto();
    }

    @Transactional(readOnly = true)
    public List<PhotoOfPresent> getPresentPhotos(Long presentId) {
        return photoOfPresentRepository.findByPresentId(presentId);
    }

    // UTILITY
    @Transactional(readOnly = true)
    public boolean existsById(Long id) {
        return presentRepository.existsById(id);
    }

    // PRIVATE METHODS
    private void addPhotosToPresent(Present present, List<MultipartFile> photos) {
        for (MultipartFile photoFile : photos) {
            if (photoFile != null && !photoFile.isEmpty()) {
                try {
                    PhotoOfPresent photo = new PhotoOfPresent();
                    photo.setPhoto(photoFile.getBytes());
                    photo.setPresent(present);
                    photoOfPresentRepository.save(photo);
                } catch (IOException e) {
                    throw new RuntimeException("Failed to process photo: " + e.getMessage(), e);
                }
            }
        }
    }

    private void validateCreateRequest(PresentUpdateRequest request) {
        if (request.getName() == null || request.getName().trim().isEmpty()) {
            throw new RuntimeException("Name is required");
        }
        if (request.getPriceCoins() == null) {
            throw new RuntimeException("Price coins is required");
        }
        if (request.getStock() == null) {
            throw new RuntimeException("Stock is required");
        }
    }
}