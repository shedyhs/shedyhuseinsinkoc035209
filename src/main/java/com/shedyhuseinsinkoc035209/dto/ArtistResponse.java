package com.shedyhuseinsinkoc035209.dto;

import com.shedyhuseinsinkoc035209.entity.Artist;
import com.shedyhuseinsinkoc035209.entity.ArtistType;

import java.time.LocalDateTime;
import java.util.UUID;

public record ArtistResponse(
        UUID id,
        String name,
        ArtistType type,
        LocalDateTime createdAt,
        LocalDateTime updatedAt
) {

    public static ArtistResponse fromEntity(Artist artist) {
        return new ArtistResponse(
                artist.getId(),
                artist.getName(),
                artist.getType(),
                artist.getCreatedAt(),
                artist.getUpdatedAt()
        );
    }
}
