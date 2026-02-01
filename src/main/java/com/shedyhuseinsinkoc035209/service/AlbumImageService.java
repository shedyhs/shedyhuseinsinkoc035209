package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.dto.AlbumImageResponse;
import com.shedyhuseinsinkoc035209.entity.Album;
import com.shedyhuseinsinkoc035209.entity.AlbumImage;
import com.shedyhuseinsinkoc035209.exception.ResourceNotFoundException;
import com.shedyhuseinsinkoc035209.repository.AlbumImageRepository;
import com.shedyhuseinsinkoc035209.repository.AlbumRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
public class AlbumImageService {

    private final AlbumImageRepository albumImageRepository;
    private final AlbumRepository albumRepository;
    private final MinioService minioService;

    public AlbumImageService(AlbumImageRepository albumImageRepository, AlbumRepository albumRepository,
                             MinioService minioService) {
        this.albumImageRepository = albumImageRepository;
        this.albumRepository = albumRepository;
        this.minioService = minioService;
    }

    @Transactional
    public List<AlbumImageResponse> uploadImages(UUID albumId, MultipartFile[] files) {
        Album album = albumRepository.findById(albumId)
                .orElseThrow(() -> new ResourceNotFoundException("Album not found with id: " + albumId));

        List<AlbumImageResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {
            String objectKey = minioService.uploadFile(file);

            AlbumImage image = AlbumImage.create(album, file.getOriginalFilename(), objectKey, file.getContentType());

            AlbumImage saved = albumImageRepository.save(image);
            String presignedUrl = minioService.getPresignedUrl(objectKey);

            responses.add(AlbumImageResponse.fromEntity(saved, albumId, presignedUrl));
        }

        return responses;
    }

    public List<AlbumImageResponse> getImagesByAlbumId(UUID albumId) {
        if (!albumRepository.existsById(albumId)) {
            throw new ResourceNotFoundException("Album not found with id: " + albumId);
        }

        List<AlbumImage> images = albumImageRepository.findByAlbumId(albumId);
        List<AlbumImageResponse> responses = new ArrayList<>();

        for (AlbumImage image : images) {
            String presignedUrl = minioService.getPresignedUrl(image.getObjectKey());
            responses.add(AlbumImageResponse.fromEntity(image, albumId, presignedUrl));
        }

        return responses;
    }

    @Transactional
    public void deleteImage(UUID imageId) {
        AlbumImage image = albumImageRepository.findById(imageId)
                .orElseThrow(() -> new ResourceNotFoundException("Image not found with id: " + imageId));

        minioService.deleteFile(image.getObjectKey());
        albumImageRepository.delete(image);
    }
}
