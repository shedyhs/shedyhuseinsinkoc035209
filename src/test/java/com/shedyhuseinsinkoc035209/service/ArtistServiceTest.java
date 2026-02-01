package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.dto.ArtistRequest;
import com.shedyhuseinsinkoc035209.dto.ArtistResponse;
import com.shedyhuseinsinkoc035209.entity.Artist;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import com.shedyhuseinsinkoc035209.exception.ResourceNotFoundException;
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
import org.springframework.test.util.ReflectionTestUtils;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ArtistServiceTest {

    @Mock
    private ArtistRepository artistRepository;

    @InjectMocks
    private ArtistService artistService;

    private Artist artist;
    private UUID artistId;

    @BeforeEach
    void setUp() {
        artistId = UUID.randomUUID();
        artist = new Artist(artistId, "Test Artist", ArtistType.SOLO);
        ReflectionTestUtils.setField(artist, "createdAt", LocalDateTime.now());
        ReflectionTestUtils.setField(artist, "updatedAt", LocalDateTime.now());
    }

    @Test
    void create_shouldReturnArtistResponse() {
        ArtistRequest request = new ArtistRequest("Test Artist", ArtistType.SOLO);
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);

        ArtistResponse response = artistService.create(request);

        assertThat(response.name()).isEqualTo("Test Artist");
        assertThat(response.type()).isEqualTo(ArtistType.SOLO);
        verify(artistRepository).save(any(Artist.class));
    }

    @Test
    void findById_shouldReturnArtistResponse() {
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));

        ArtistResponse response = artistService.findById(artistId);

        assertThat(response.id()).isEqualTo(artistId);
        assertThat(response.name()).isEqualTo("Test Artist");
    }

    @Test
    void findById_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(artistRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.findById(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artist not found");
    }

    @Test
    void findAll_shouldReturnPageOfArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = new PageImpl<>(List.of(artist));
        when(artistRepository.findAll(any(Pageable.class))).thenReturn(page);

        Page<ArtistResponse> response = artistService.findAll(pageable);

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getContent().get(0).name()).isEqualTo("Test Artist");
    }

    @Test
    void findByName_withAscOrder_shouldReturnFilteredArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = new PageImpl<>(List.of(artist));
        when(artistRepository.findByNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(page);

        Page<ArtistResponse> response = artistService.findByName("Test", "asc", pageable);

        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void findByName_withDescOrder_shouldReturnFilteredArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = new PageImpl<>(List.of(artist));
        when(artistRepository.findByNameContainingIgnoreCase(any(String.class), any(Pageable.class))).thenReturn(page);

        Page<ArtistResponse> response = artistService.findByName("Test", "desc", pageable);

        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void findByType_shouldReturnFilteredArtists() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Artist> page = new PageImpl<>(List.of(artist));
        when(artistRepository.findByType(ArtistType.SOLO, pageable)).thenReturn(page);

        Page<ArtistResponse> response = artistService.findByType(ArtistType.SOLO, pageable);

        assertThat(response.getContent()).hasSize(1);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        ArtistRequest request = new ArtistRequest("Updated", ArtistType.BAND);
        when(artistRepository.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> artistService.update(id, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artist not found");
    }

    @Test
    void update_shouldReturnUpdatedArtist() {
        ArtistRequest request = new ArtistRequest("Updated Artist", ArtistType.BAND);
        when(artistRepository.findById(artistId)).thenReturn(Optional.of(artist));
        when(artistRepository.save(any(Artist.class))).thenReturn(artist);

        ArtistResponse response = artistService.update(artistId, request);

        assertThat(response).isNotNull();
        verify(artistRepository).save(any(Artist.class));
    }

    @Test
    void delete_shouldDeleteArtist() {
        when(artistRepository.existsById(artistId)).thenReturn(true);

        artistService.delete(artistId);

        verify(artistRepository).deleteById(artistId);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        UUID id = UUID.randomUUID();
        when(artistRepository.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> artistService.delete(id))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessageContaining("Artist not found");
    }
}
