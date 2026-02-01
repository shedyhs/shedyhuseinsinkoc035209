package com.shedyhuseinsinkoc035209.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "album_images")
public class AlbumImage {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "album_id", nullable = false)
    private Album album;

    @Column(name = "file_name", nullable = false)
    private String fileName;

    @Column(name = "object_key", nullable = false)
    private String objectKey;

    @Column(name = "content_type")
    private String contentType;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    public AlbumImage() {
    }

    public AlbumImage(UUID id, Album album, String fileName, String objectKey, String contentType) {
        this.id = id;
        this.album = album;
        this.fileName = fileName;
        this.objectKey = objectKey;
        this.contentType = contentType;
    }

    public static AlbumImage create(Album album, String fileName, String objectKey, String contentType) {
        AlbumImage image = new AlbumImage();
        image.album = album;
        image.fileName = fileName;
        image.objectKey = objectKey;
        image.contentType = contentType;
        return image;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Album getAlbum() {
        return album;
    }

    public void setAlbum(Album album) {
        this.album = album;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getObjectKey() {
        return objectKey;
    }

    public void setObjectKey(String objectKey) {
        this.objectKey = objectKey;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
