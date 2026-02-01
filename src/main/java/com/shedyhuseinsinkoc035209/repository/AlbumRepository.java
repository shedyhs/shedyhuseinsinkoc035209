package com.shedyhuseinsinkoc035209.repository;

import com.shedyhuseinsinkoc035209.entity.Album;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface AlbumRepository extends JpaRepository<Album, UUID> {

    @Query("SELECT DISTINCT a FROM Album a JOIN a.artists ar WHERE ar.type = :type")
    Page<Album> findByArtistType(@Param("type") ArtistType type, Pageable pageable);

    @Query("SELECT DISTINCT a FROM Album a JOIN a.artists ar WHERE LOWER(ar.name) LIKE LOWER(CONCAT('%', :name, '%'))")
    Page<Album> findByArtistNameContaining(@Param("name") String name, Pageable pageable);
}
