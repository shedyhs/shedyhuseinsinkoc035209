package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.exception.InfrastructureException;
import io.minio.BucketExistsArgs;
import io.minio.GetPresignedObjectUrlArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.http.Method;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
public class MinioService {

    private final MinioClient minioClient;

    @Value("${minio.bucket}")
    private String bucket;

    public MinioService(MinioClient minioClient) {
        this.minioClient = minioClient;
    }

    @PostConstruct
    public void init() {
        try {
            boolean exists = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!exists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }
        } catch (Exception e) {
            throw new InfrastructureException("Failed to initialize MinIO bucket: " + e.getMessage(), e);
        }
    }

    public String uploadFile(MultipartFile file) {
        try {
            String objectKey = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
            InputStream inputStream = file.getInputStream();

            minioClient.putObject(PutObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .stream(inputStream, file.getSize(), -1)
                    .contentType(file.getContentType())
                    .build());

            return objectKey;
        } catch (Exception e) {
            throw new InfrastructureException("Failed to upload file to MinIO: " + e.getMessage(), e);
        }
    }

    public String getPresignedUrl(String objectKey) {
        try {
            return minioClient.getPresignedObjectUrl(GetPresignedObjectUrlArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .method(Method.GET)
                    .expiry(30, TimeUnit.MINUTES)
                    .build());
        } catch (Exception e) {
            throw new InfrastructureException("Failed to generate presigned URL: " + e.getMessage(), e);
        }
    }

    public void deleteFile(String objectKey) {
        try {
            minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(bucket)
                    .object(objectKey)
                    .build());
        } catch (Exception e) {
            throw new InfrastructureException("Failed to delete file from MinIO: " + e.getMessage(), e);
        }
    }
}
