package com.shedyhuseinsinkoc035209.dto;

import com.shedyhuseinsinkoc035209.entity.Artist;
import com.shedyhuseinsinkoc035209.entity.ArtistType;

import java.time.LocalDateTime;
import java.util.UUID;

public class ArtistResponse {

    private UUID id;
    private String name;
    private ArtistType type;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public ArtistResponse() {
    }

    public ArtistResponse(UUID id, String name, ArtistType type, LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.name = name;
        this.type = type;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static ArtistResponse fromEntity(Artist artist) {
        return new ArtistResponse(
                artist.getId(),
                artist.getName(),
                artist.getType(),
                artist.getCreatedAt(),
                artist.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArtistType getType() {
        return type;
    }

    public void setType(ArtistType type) {
        this.type = type;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}
