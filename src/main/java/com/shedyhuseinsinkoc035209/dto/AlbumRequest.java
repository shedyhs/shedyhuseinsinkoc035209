package com.shedyhuseinsinkoc035209.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

import java.util.Set;
import java.util.UUID;

public record AlbumRequest(
        @NotBlank String title,
        Integer releaseYear,
        @NotEmpty Set<UUID> artistIds
) {
}
