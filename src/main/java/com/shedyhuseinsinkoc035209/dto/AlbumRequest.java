package com.shedyhuseinsinkoc035209.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.UUID;

public class AlbumRequest {

    @NotBlank
    private String title;

    private Integer releaseYear;

    @NotEmpty
    private Set<UUID> artistIds;

    public AlbumRequest() {
    }

    public AlbumRequest(String title, Integer releaseYear, Set<UUID> artistIds) {
        this.title = title;
        this.releaseYear = releaseYear;
        this.artistIds = artistIds;
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

    public Set<UUID> getArtistIds() {
        return artistIds;
    }

    public void setArtistIds(Set<UUID> artistIds) {
        this.artistIds = artistIds;
    }
}
