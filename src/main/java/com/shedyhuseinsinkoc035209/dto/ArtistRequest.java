package com.shedyhuseinsinkoc035209.dto;

import com.shedyhuseinsinkoc035209.entity.ArtistType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class ArtistRequest {

    @NotBlank
    private String name;

    @NotNull
    private ArtistType type;

    public ArtistRequest() {
    }

    public ArtistRequest(String name, ArtistType type) {
        this.name = name;
        this.type = type;
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
}
