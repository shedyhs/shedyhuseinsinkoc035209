package com.shedyhuseinsinkoc035209.dto;

import com.shedyhuseinsinkoc035209.entity.Album;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public class AlbumResponse {

    private UUID id;
    private String title;
    private Integer releaseYear;
    private List<String> artistNames;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public AlbumResponse() {
    }

    public AlbumResponse(UUID id, String title, Integer releaseYear, List<String> artistNames,
                         LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.title = title;
        this.releaseYear = releaseYear;
        this.artistNames = artistNames;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }

    public static AlbumResponse fromEntity(Album album) {
        return new AlbumResponse(
                album.getId(),
                album.getTitle(),
                album.getReleaseYear(),
                album.getArtistNames(),
                album.getCreatedAt(),
                album.getUpdatedAt()
        );
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getReleaseYear() {
        return releaseYear;
    }

    public void setReleaseYear(Integer releaseYear) {
        this.releaseYear = releaseYear;
    }

    public List<String> getArtistNames() {
        return artistNames;
    }

    public void setArtistNames(List<String> artistNames) {
        this.artistNames = artistNames;
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
