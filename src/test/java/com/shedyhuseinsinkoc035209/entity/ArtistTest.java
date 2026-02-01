package com.shedyhuseinsinkoc035209.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class ArtistTest {

    @Test
    void defaultConstructor_shouldCreateEmptyArtist() {
        Artist artist = new Artist();

        assertThat(artist.getId()).isNull();
        assertThat(artist.getName()).isNull();
        assertThat(artist.getType()).isNull();
        assertThat(artist.getAlbums()).isEmpty();
    }

    @Test
    void constructor_withNameAndType_shouldSetFields() {
        Artist artist = new Artist("Serj Tankian", ArtistType.SOLO);

        assertThat(artist.getName()).isEqualTo("Serj Tankian");
        assertThat(artist.getType()).isEqualTo(ArtistType.SOLO);
        assertThat(artist.getId()).isNull();
    }

    @Test
    void constructor_withIdNameAndType_shouldSetAllFields() {
        UUID id = UUID.randomUUID();
        Artist artist = new Artist(id, "Guns N' Roses", ArtistType.BAND);

        assertThat(artist.getId()).isEqualTo(id);
        assertThat(artist.getName()).isEqualTo("Guns N' Roses");
        assertThat(artist.getType()).isEqualTo(ArtistType.BAND);
    }

    @Test
    void update_shouldChangeNameAndType() {
        Artist artist = new Artist("Old Name", ArtistType.SOLO);

        artist.update("New Name", ArtistType.BAND);

        assertThat(artist.getName()).isEqualTo("New Name");
        assertThat(artist.getType()).isEqualTo(ArtistType.BAND);
    }

    @Test
    void addAlbum_shouldCreateBidirectionalRelationship() {
        Artist artist = new Artist("Mike Shinoda", ArtistType.SOLO);
        Album album = new Album("Post Traumatic", 2018);

        artist.addAlbum(album);

        assertThat(artist.getAlbums()).contains(album);
        assertThat(album.getArtists()).contains(artist);
    }

    @Test
    void removeAlbum_shouldRemoveBidirectionalRelationship() {
        Artist artist = new Artist("Mike Shinoda", ArtistType.SOLO);
        Album album = new Album("Post Traumatic", 2018);
        artist.addAlbum(album);

        artist.removeAlbum(album);

        assertThat(artist.getAlbums()).doesNotContain(album);
        assertThat(album.getArtists()).doesNotContain(artist);
    }

    @Test
    void onCreate_shouldSetTimestamps() {
        Artist artist = new Artist("Test", ArtistType.SOLO);

        artist.onCreate();

        assertThat(artist.getCreatedAt()).isNotNull();
        assertThat(artist.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_shouldSetUpdatedAt() {
        Artist artist = new Artist("Test", ArtistType.SOLO);
        artist.onCreate();

        artist.onUpdate();

        assertThat(artist.getUpdatedAt()).isNotNull();
    }
}
