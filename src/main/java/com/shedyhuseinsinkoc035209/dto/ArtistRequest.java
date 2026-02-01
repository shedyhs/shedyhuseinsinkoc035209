package com.shedyhuseinsinkoc035209.dto;

import com.shedyhuseinsinkoc035209.entity.ArtistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record ArtistRequest(
        @NotBlank String name,
        @NotNull ArtistType type
) {
}
