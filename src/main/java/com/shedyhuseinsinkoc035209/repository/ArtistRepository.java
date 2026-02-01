package com.shedyhuseinsinkoc035209.repository;

import com.shedyhuseinsinkoc035209.entity.Artist;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface ArtistRepository extends JpaRepository<Artist, UUID> {

    Page<Artist> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Artist> findByType(ArtistType type, Pageable pageable);

    Page<Artist> findByNameContainingIgnoreCaseAndType(String name, ArtistType type, Pageable pageable);
}
