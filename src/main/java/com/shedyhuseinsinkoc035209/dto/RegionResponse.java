package com.shedyhuseinsinkoc035209.dto;

import com.shedyhuseinsinkoc035209.entity.Region;

public record RegionResponse(
        Long id,
        Integer externalId,
        String name,
        Boolean active
) {

    public static RegionResponse fromEntity(Region region) {
        return new RegionResponse(
                region.getId(),
                region.getExternalId(),
                region.getName(),
                region.getActive()
        );
    }
}
