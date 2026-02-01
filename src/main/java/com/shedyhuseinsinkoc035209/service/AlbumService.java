package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.dto.AlbumRequest;
import com.shedyhuseinsinkoc035209.dto.AlbumResponse;
import com.shedyhuseinsinkoc035209.entity.Album;
import com.shedyhuseinsinkoc035209.entity.Artist;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import com.shedyhuseinsinkoc035209.repository.AlbumRepository;
import com.shedyhuseinsinkoc035209.repository.ArtistRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class AlbumService {

    private final AlbumRepository albumRepository;
    private final ArtistRepository artistRepository;
    private final SimpMessagingTemplate messagingTemplate;

    public AlbumService(AlbumRepository albumRepository, ArtistRepository artistRepository,
                       SimpMessagingTemplate messagingTemplate) {
        this.albumRepository = albumRepository;
        this.artistRepository = artistRepository;
        this.messagingTemplate = messagingTemplate;
    }

    @Transactional
    public AlbumResponse create(AlbumRequest request) {
        Album album = new Album(request.getTitle(), request.getReleaseYear());

        for (UUID artistId : request.getArtistIds()) {
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Artist not found with id: " + artistId));
            album.addArtist(artist);
        }

        Album saved = albumRepository.save(album);

        AlbumResponse response = AlbumResponse.fromEntity(saved);
        return response;
    }

    public AlbumResponse findById(UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + id));
        return AlbumResponse.fromEntity(album);
    }

    public Page<AlbumResponse> findAll(Pageable pageable) {
        return albumRepository.findAll(pageable).map(AlbumResponse::fromEntity);
    }

    public Page<AlbumResponse> findByArtistType(ArtistType type, Pageable pageable) {
        return albumRepository.findByArtistType(type, pageable).map(AlbumResponse::fromEntity);
    }

    public Page<AlbumResponse> findByArtistName(String name, String order, Pageable pageable) {
        Sort.Direction direction = "desc".equalsIgnoreCase(order) ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable sorted = PageRequest.of(
                pageable.getPageNumber(),
                pageable.getPageSize(),
                Sort.by(direction, "title")
        );
        return albumRepository.findByArtistNameContaining(name, sorted).map(AlbumResponse::fromEntity);
    }

    @Transactional
    public AlbumResponse update(UUID id, AlbumRequest request) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + id));

        album.update(request.getTitle(), request.getReleaseYear());
        album.clearArtists();

        for (UUID artistId : request.getArtistIds()) {
            Artist artist = artistRepository.findById(artistId)
                    .orElseThrow(() -> new RuntimeException("Artist not found with id: " + artistId));
            album.addArtist(artist);
        }

        Album updated = albumRepository.save(album);
        return AlbumResponse.fromEntity(updated);
    }

    @Transactional
    public void delete(UUID id) {
        Album album = albumRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Album not found with id: " + id));

        album.clearArtists();
        albumRepository.delete(album);
    }
}
