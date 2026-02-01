package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.dto.AlbumImageResponse;
import com.shedyhuseinsinkoc035209.entity.Album;
import com.shedyhuseinsinkoc035209.entity.AlbumImage;
import com.shedyhuseinsinkoc035209.exception.ResourceNotFoundException;
import com.shedyhuseinsinkoc035209.repository.AlbumImageRepository;
import com.shedyhuseinsinkoc035209.repository.AlbumRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumImageServiceTest {

    @Mock
    private AlbumImageRepository albumImageRepository;

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private MinioService minioService;

    @InjectMocks
    private AlbumImageService albumImageService;

    private UUID albumId;
    private Album album;

    @BeforeEach
    void setUp() {
        albumId = UUID.randomUUID();
        album = new Album(albumId, "Test Album", null);
    }

    @Test
    void uploadImages_shouldReturnImageResponses() {
        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", "image/jpeg", "data".getBytes());
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(minioService.uploadFile(file)).thenReturn("uuid_test.jpg");
        when(minioService.getPresignedUrl("uuid_test.jpg")).thenReturn("http://minio/presigned");

        AlbumImage savedImage = new AlbumImage(UUID.randomUUID(), album, "test.jpg", "uuid_test.jpg", "image/jpeg");
        ReflectionTestUtils.setField(savedImage, "createdAt", LocalDateTime.now());
        when(albumImageRepository.save(any(AlbumImage.class))).thenReturn(savedImage);

        List<AlbumImageResponse> responses = albumImageService.uploadImages(albumId, new MockMultipartFile[]{file});

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).fileName()).isEqualTo("test.jpg");
        assertThat(responses.get(0).url()).isEqualTo("http://minio/presigned");
    }

    @Test
    void uploadImages_shouldThrowWhenAlbumNotFound() {
        UUID invalidId = UUID.randomUUID();
        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", "image/jpeg", "data".getBytes());
        when(albumRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumImageService.uploadImages(invalidId, new MockMultipartFile[]{file}))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Album not found");
    }

    @Test
    void getImagesByAlbumId_shouldReturnImages() {
        when(albumRepository.existsById(albumId)).thenReturn(true);

        AlbumImage image = new AlbumImage(UUID.randomUUID(), album, "test.jpg", "uuid_test.jpg", "image/jpeg");
        ReflectionTestUtils.setField(image, "createdAt", LocalDateTime.now());
        when(albumImageRepository.findByAlbumId(albumId)).thenReturn(List.of(image));
        when(minioService.getPresignedUrl("uuid_test.jpg")).thenReturn("http://minio/presigned");

        List<AlbumImageResponse> responses = albumImageService.getImagesByAlbumId(albumId);

        assertThat(responses).hasSize(1);
        assertThat(responses.get(0).fileName()).isEqualTo("test.jpg");
    }

    @Test
    void getImagesByAlbumId_shouldThrowWhenAlbumNotFound() {
        UUID invalidId = UUID.randomUUID();
        when(albumRepository.existsById(invalidId)).thenReturn(false);

        assertThatThrownBy(() -> albumImageService.getImagesByAlbumId(invalidId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Album not found");
    }

    @Test
    void deleteImage_shouldDeleteFromMinioAndDb() {
        UUID imageId = UUID.randomUUID();
        AlbumImage image = new AlbumImage(imageId, album, "test.jpg", "uuid_test.jpg", "image/jpeg");
        when(albumImageRepository.findById(imageId)).thenReturn(Optional.of(image));

        albumImageService.deleteImage(imageId);

        verify(minioService).deleteFile("uuid_test.jpg");
        verify(albumImageRepository).delete(image);
    }

    @Test
    void deleteImage_shouldThrowWhenImageNotFound() {
        UUID invalidId = UUID.randomUUID();
        when(albumImageRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumImageService.deleteImage(invalidId))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Image not found");
    }
}
