package com.shedyhuseinsinkoc035209.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "regions")
public class Region {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "external_id", nullable = false)
    private Integer externalId;

    @Column(nullable = false, length = 200)
    private String name;

    @Column(nullable = false)
    private Boolean active = true;

    public Region() {
    }

    public Region(Integer externalId, String name, Boolean active) {
        this.externalId = externalId;
        this.name = name;
        this.active = active;
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

    public void deactivate() {
        this.active = false;
    }

    public boolean hasNameChanged(String newName) {
        return !this.name.equals(newName);
    }
}
