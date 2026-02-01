package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.exception.InfrastructureException;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MinioServiceTest {

    @Mock
    private MinioClient minioClient;

    private MinioService minioService;

    @BeforeEach
    void setUp() {
        minioService = new MinioService(minioClient);
        ReflectionTestUtils.setField(minioService, "bucket", "album-images");
    }

    @Test
    void init_shouldCreateBucketIfNotExists() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(false);

        minioService.init();

        verify(minioClient).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void init_shouldNotCreateBucketIfExists() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenReturn(true);

        minioService.init();

        verify(minioClient, never()).makeBucket(any(MakeBucketArgs.class));
    }

    @Test
    void init_shouldThrowOnError() throws Exception {
        when(minioClient.bucketExists(any(BucketExistsArgs.class))).thenThrow(new RuntimeException("Connection failed"));

        assertThatThrownBy(() -> minioService.init())
                .isInstanceOf(InfrastructureException.class)
                .hasMessageContaining("Failed to initialize MinIO bucket");
    }

    @Test
    void uploadFile_shouldReturnObjectKey() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
        when(minioClient.putObject(any(PutObjectArgs.class))).thenReturn(null);

        String objectKey = minioService.uploadFile(file);

        assertThat(objectKey).contains("test.jpg");
        verify(minioClient).putObject(any(PutObjectArgs.class));
    }

    @Test
    void uploadFile_shouldThrowOnError() throws Exception {
        MockMultipartFile file = new MockMultipartFile("file", "test.jpg", "image/jpeg", "data".getBytes());
        when(minioClient.putObject(any(PutObjectArgs.class))).thenThrow(new RuntimeException("Upload failed"));

        assertThatThrownBy(() -> minioService.uploadFile(file))
                .isInstanceOf(InfrastructureException.class)
                .hasMessageContaining("Failed to upload file to MinIO");
    }

    @Test
    void getPresignedUrl_shouldReturnUrl() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenReturn("http://minio:9000/album-images/test.jpg?signature=abc");

        String url = minioService.getPresignedUrl("test.jpg");

        assertThat(url).contains("test.jpg");
    }

    @Test
    void getPresignedUrl_shouldThrowOnError() throws Exception {
        when(minioClient.getPresignedObjectUrl(any(GetPresignedObjectUrlArgs.class)))
                .thenThrow(new RuntimeException("URL generation failed"));

        assertThatThrownBy(() -> minioService.getPresignedUrl("test.jpg"))
                .isInstanceOf(InfrastructureException.class)
                .hasMessageContaining("Failed to generate presigned URL");
    }

    @Test
    void deleteFile_shouldRemoveObject() throws Exception {
        minioService.deleteFile("test.jpg");

        verify(minioClient).removeObject(any(RemoveObjectArgs.class));
    }

    @Test
    void deleteFile_shouldThrowOnError() throws Exception {
        doThrow(new RuntimeException("Delete failed")).when(minioClient).removeObject(any(RemoveObjectArgs.class));

        assertThatThrownBy(() -> minioService.deleteFile("test.jpg"))
                .isInstanceOf(InfrastructureException.class)
                .hasMessageContaining("Failed to delete file from MinIO");
    }
}
