package com.shedyhuseinsinkoc035209.repository;

import com.shedyhuseinsinkoc035209.entity.AlbumImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlbumImageRepository extends JpaRepository<AlbumImage, UUID> {

    List<AlbumImage> findByAlbumId(UUID albumId);
}
