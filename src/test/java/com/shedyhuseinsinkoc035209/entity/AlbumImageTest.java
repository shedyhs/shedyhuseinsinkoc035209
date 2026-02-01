package com.shedyhuseinsinkoc035209.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class AlbumImageTest {

    @Test
    void defaultConstructor_shouldCreateEmptyImage() {
        AlbumImage image = new AlbumImage();

        assertThat(image.getId()).isNull();
        assertThat(image.getAlbum()).isNull();
        assertThat(image.getFileName()).isNull();
    }

    @Test
    void constructor_withAllFields_shouldSetFields() {
        UUID id = UUID.randomUUID();
        Album album = new Album("Test Album", 2020);
        AlbumImage image = new AlbumImage(id, album, "cover.jpg", "albums/cover.jpg", "image/jpeg");

        assertThat(image.getId()).isEqualTo(id);
        assertThat(image.getAlbum()).isEqualTo(album);
        assertThat(image.getFileName()).isEqualTo("cover.jpg");
        assertThat(image.getObjectKey()).isEqualTo("albums/cover.jpg");
        assertThat(image.getContentType()).isEqualTo("image/jpeg");
    }

    @Test
    void create_factoryMethod_shouldReturnConfiguredImage() {
        Album album = new Album("Test Album", 2020);

        AlbumImage image = AlbumImage.create(album, "photo.png", "key/photo.png", "image/png");

        assertThat(image.getAlbum()).isEqualTo(album);
        assertThat(image.getFileName()).isEqualTo("photo.png");
        assertThat(image.getObjectKey()).isEqualTo("key/photo.png");
        assertThat(image.getContentType()).isEqualTo("image/png");
    }

    @Test
    void onCreate_shouldSetCreatedAt() {
        AlbumImage image = AlbumImage.create(new Album("Test", 2020), "f.jpg", "k", "image/jpeg");

        image.onCreate();

        assertThat(image.getCreatedAt()).isNotNull();
    }
}
