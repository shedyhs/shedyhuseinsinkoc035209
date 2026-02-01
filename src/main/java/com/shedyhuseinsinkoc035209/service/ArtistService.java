package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.dto.ArtistRequest;
import com.shedyhuseinsinkoc035209.dto.ArtistResponse;
import com.shedyhuseinsinkoc035209.entity.Artist;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import com.shedyhuseinsinkoc035209.exception.ResourceNotFoundException;
import com.shedyhuseinsinkoc035209.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ArtistService {

    private final ArtistRepository artistRepository;

    public ArtistService(ArtistRepository artistRepository) {
        this.artistRepository = artistRepository;
    }

    @Transactional
    public ArtistResponse create(ArtistRequest request) {
        Artist artist = new Artist(request.name(), request.type());
        Artist saved = artistRepository.save(artist);
        return ArtistResponse.fromEntity(saved);
    }

    public ArtistResponse findById(UUID id) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + id));
        return ArtistResponse.fromEntity(artist);
    }

    public Page<ArtistResponse> findAll(Pageable pageable) {
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(Sort.Direction.ASC, "name")
        );
        return artistRepository.findAll(sorted).map(ArtistResponse::fromEntity);
    }

    public Page<ArtistResponse> findByName(String name, String order, Pageable pageable) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(direction, "name")
        );
        return artistRepository.findByNameContainingIgnoreCase(name, sorted).map(ArtistResponse::fromEntity);
    }

    public Page<ArtistResponse> findByType(ArtistType type, Pageable pageable) {
        return artistRepository.findByType(type, pageable).map(ArtistResponse::fromEntity);
    }

    @Transactional
    public ArtistResponse update(UUID id, ArtistRequest request) {
        Artist artist = artistRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Artist not found with id: " + id));
        artist.update(request.name(), request.type());
        Artist updated = artistRepository.save(artist);
        return ArtistResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(UUID id) {
        if (!artistRepository.existsById(id)) {
            throw new ResourceNotFoundException("Artist not found with id: " + id);
        }
        artistRepository.deleteById(id);
    }
}
