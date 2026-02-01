package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.dto.AlbumRequest;
import com.shedyhuseinsinkoc035209.dto.AlbumResponse;
import com.shedyhuseinsinkoc035209.entity.Album;
import com.shedyhuseinsinkoc035209.entity.Artist;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import com.shedyhuseinsinkoc035209.repository.AlbumRepository;
import com.shedyhuseinsinkoc035209.repository.ArtistRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AlbumServiceTest {

    @Mock
    private AlbumRepository albumRepository;

    @Mock
    private ArtistRepository artistRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private AlbumService albumService;

    private Album album;
    private Artist artist;
    private UUID albumId;
    private UUID artistId;

    @BeforeEach
    void setUp() {
        albumId = UUID.randomUUID();
        artistId = UUID.randomUUID();

        artist = new Artist();
        artist.setId(artistId);
        artist.setName("Test Artist");
        artist.setType(ArtistType.SOLO);
        artist.setAlbums(new HashSet<>());

        album = new Album();
        album.setId(albumId);
        album.setTitle("Test Album");
        album.setReleaseYear(2023);
        album.setArtists(new HashSet<>(Set.of(artist)));
        album.setCreatedAt(LocalDateTime.now());
        album.setUpdatedAt(LocalDateTime.now());
    }

    @Test
    void create_shouldReturnAlbumResponseAndSendWebSocket() {
        AlbumRequest request = new AlbumRequest("Test Album", 2023, Set.of(artistId));
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        AlbumResponse response = albumService.create(request);

        assertThat(response.getTitle()).isEqualTo("Test Album");
        verify(messagingTemplate).convertAndSend(eq("/topic/albums"), any(AlbumResponse.class));
    }

    @Test
    void create_shouldThrowWhenArtistNotFound() {
        UUID invalidId = UUID.randomUUID();
        AlbumRequest request = new AlbumRequest("Test Album", 2023, Set.of(invalidId));
        when(artistRepository.findById(invalidId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.create(request))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Artist not found");
    }

    @Test
    void findById_shouldReturnAlbumResponse() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        AlbumResponse response = albumService.findById(albumId);

        assertThat(response.getId()).isEqualTo(albumId);
        assertThat(response.getTitle()).isEqualTo("Test Album");
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(albumRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> albumService.findById(id))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Album not found");
    }

    @Test
    void findAll_shouldReturnPageOfAlbums() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Album> page = new PageImpl<>(List.of(album));
        when(albumRepository.findAll(pageable)).thenReturn(page);

        Page<AlbumResponse> response = albumService.findAll(pageable);

        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void findByArtistType_shouldReturnFilteredAlbums() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Album> page = new PageImpl<>(List.of(album));
        when(albumRepository.findByArtistType(ArtistType.SOLO, pageable)).thenReturn(page);

        Page<AlbumResponse> response = albumService.findByArtistType(ArtistType.SOLO, pageable);

        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void update_shouldReturnUpdatedAlbum() {
        AlbumRequest request = new AlbumRequest("Updated Album", 2024, Set.of(artistId));
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(albumRepository.save(any(Album.class))).thenReturn(album);

        AlbumResponse response = albumService.update(albumId, request);

        assertThat(response).isNotNull();
        verify(albumRepository).save(any(Album.class));
    }

    @Test
    void delete_shouldDeleteAlbum() {
        when(albumRepository.findById(albumId)).thenReturn(Optional.of(album));

        albumService.delete(albumId);

        verify(albumRepository).delete(album);
    }
}
