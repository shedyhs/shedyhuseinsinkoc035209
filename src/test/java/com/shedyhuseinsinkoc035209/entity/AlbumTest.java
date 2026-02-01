package com.shedyhuseinsinkoc035209.entity;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumTest {

    @Test
    void defaultConstructor_shouldCreateEmptyAlbum() {
        Album album = new Album();

        assertThat(album.getId()).isNull();
        assertThat(album.getTitle()).isNull();
        assertThat(album.getReleaseYear()).isNull();
        assertThat(album.getArtists()).isEmpty();
        assertThat(album.getImages()).isEmpty();
    }

    @Test
    void constructor_withTitleAndYear_shouldSetFields() {
        Album album = new Album("Harakiri", 2012);

        assertThat(album.getTitle()).isEqualTo("Harakiri");
        assertThat(album.getReleaseYear()).isEqualTo(2012);
    }

    @Test
    void constructor_withIdTitleAndYear_shouldSetAllFields() {
        UUID id = UUID.randomUUID();
        Album album = new Album(id, "Harakiri", 2012);

        assertThat(album.getId()).isEqualTo(id);
        assertThat(album.getTitle()).isEqualTo("Harakiri");
        assertThat(album.getReleaseYear()).isEqualTo(2012);
    }

    @Test
    void update_shouldChangeTitleAndYear() {
        Album album = new Album("Old Title", 2000);

        album.update("New Title", 2024);

        assertThat(album.getTitle()).isEqualTo("New Title");
        assertThat(album.getReleaseYear()).isEqualTo(2024);
    }

    @Test
    void addArtist_shouldCreateBidirectionalRelationship() {
        Album album = new Album("Greatest Hits", 2004);
        Artist artist = new Artist("Guns N' Roses", ArtistType.BAND);

        album.addArtist(artist);

        assertThat(album.getArtists()).contains(artist);
        assertThat(artist.getAlbums()).contains(album);
    }

    @Test
    void removeArtist_shouldRemoveBidirectionalRelationship() {
        Album album = new Album("Greatest Hits", 2004);
        Artist artist = new Artist("Guns N' Roses", ArtistType.BAND);
        album.addArtist(artist);

        album.removeArtist(artist);

        assertThat(album.getArtists()).doesNotContain(artist);
        assertThat(artist.getAlbums()).doesNotContain(album);
    }

    @Test
    void clearArtists_shouldRemoveAllBidirectionalRelationships() {
        Album album = new Album("Greatest Hits", 2004);
        Artist artist1 = new Artist("Artist 1", ArtistType.SOLO);
        Artist artist2 = new Artist("Artist 2", ArtistType.BAND);
        album.addArtist(artist1);
        album.addArtist(artist2);

        album.clearArtists();

        assertThat(album.getArtists()).isEmpty();
        assertThat(artist1.getAlbums()).doesNotContain(album);
        assertThat(artist2.getAlbums()).doesNotContain(album);
    }

    @Test
    void getArtistNames_shouldReturnSortedNames() {
        Album album = new Album("Greatest Hits", 2004);
        Artist artistB = new Artist("Zed", ArtistType.SOLO);
        Artist artistA = new Artist("Alice", ArtistType.SOLO);
        album.addArtist(artistB);
        album.addArtist(artistA);

        List<String> names = album.getArtistNames();

        assertThat(names).containsExactly("Alice", "Zed");
    }

    @Test
    void getArtistNames_shouldReturnEmptyListWhenNoArtists() {
        Album album = new Album("Solo Album", 2020);

        List<String> names = album.getArtistNames();

        assertThat(names).isEmpty();
    }

    @Test
    void onCreate_shouldSetTimestamps() {
        Album album = new Album("Test", 2020);

        album.onCreate();

        assertThat(album.getCreatedAt()).isNotNull();
        assertThat(album.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_shouldSetUpdatedAt() {
        Album album = new Album("Test", 2020);
        album.onCreate();

        album.onUpdate();

        assertThat(album.getUpdatedAt()).isNotNull();
    }
}
