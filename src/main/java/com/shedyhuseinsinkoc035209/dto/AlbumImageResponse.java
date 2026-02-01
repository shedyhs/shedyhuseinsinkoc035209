package com.shedyhuseinsinkoc035209.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class AlbumImageResponse {

    private UUID id;
    private UUID albumId;
    private String fileName;
    private String contentType;
    private String url;
    private LocalDateTime createdAt;

    public AlbumImageResponse() {
    }

    public AlbumImageResponse(UUID id, UUID albumId, String fileName, String contentType, String url,
                              LocalDateTime createdAt) {
        this.id = id;
        this.albumId = albumId;
        this.fileName = fileName;
        this.contentType = contentType;
        this.url = url;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getAlbumId() {
        return albumId;
    }

    public void setAlbumId(UUID albumId) {
        this.albumId = albumId;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
