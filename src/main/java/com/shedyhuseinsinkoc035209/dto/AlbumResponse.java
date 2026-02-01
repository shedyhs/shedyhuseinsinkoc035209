package com.shedyhuseinsinkoc035209.dto;

import com.shedyhuseinsinkoc035209.entity.Album;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public record AlbumResponse(
        UUID id,
        String title,
        Integer releaseYear,
        List<String> artistNames,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

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
}
