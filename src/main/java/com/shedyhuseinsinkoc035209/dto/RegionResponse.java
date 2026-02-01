package com.shedyhuseinsinkoc035209.dto;

import com.shedyhuseinsinkoc035209.entity.Region;

public class RegionResponse {

    private Long id;
    private Integer externalId;
    private String name;
    private Boolean active;

    public RegionResponse() {
    }

    public RegionResponse(Long id, Integer externalId, String name, Boolean active) {
        this.id = id;
        this.externalId = externalId;
        this.name = name;
        this.active = active;
    }

    public static RegionResponse fromEntity(Region region) {
        return new RegionResponse(
                region.getId(),
                region.getExternalId(),
                region.getName(),
                region.getActive()
        );
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Integer getExternalId() {
        return externalId;
    }

    public void setExternalId(Integer externalId) {
        this.externalId = externalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
