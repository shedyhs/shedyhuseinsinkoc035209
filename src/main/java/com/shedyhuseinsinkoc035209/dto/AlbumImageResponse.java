package com.shedyhuseinsinkoc035209.dto;

import com.shedyhuseinsinkoc035209.entity.AlbumImage;

import java.time.LocalDateTime;
import java.util.UUID;

public record AlbumImageResponse(
        UUID id,
        UUID albumId,
        String fileName,
        String contentType,
        String url,
        LocalDateTime createdAt
) {

    public static AlbumImageResponse fromEntity(AlbumImage image, UUID albumId, String url) {
        return new AlbumImageResponse(
                image.getId(),
                albumId,
                image.getFileName(),
                image.getContentType(),
                url,
                image.getCreatedAt()
        );
    }
}
