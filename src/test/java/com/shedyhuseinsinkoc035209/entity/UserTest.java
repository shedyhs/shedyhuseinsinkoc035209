package com.shedyhuseinsinkoc035209.entity;

import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

class UserTest {

    @Test
    void defaultConstructor_shouldCreateEmptyUser() {
        User user = new User();

        assertThat(user.getId()).isNull();
        assertThat(user.getUsername()).isNull();
        assertThat(user.getPassword()).isNull();
        assertThat(user.getRole()).isNull();
    }

    @Test
    void constructor_withAllFields_shouldSetFields() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "admin", "hashedPassword", Role.ADMIN);

        assertThat(user.getId()).isEqualTo(id);
        assertThat(user.getUsername()).isEqualTo("admin");
        assertThat(user.getPassword()).isEqualTo("hashedPassword");
        assertThat(user.getRole()).isEqualTo(Role.ADMIN);
    }

    @Test
    void onCreate_shouldSetTimestamps() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "admin", "pass", Role.USER);

        user.onCreate();

        assertThat(user.getCreatedAt()).isNotNull();
        assertThat(user.getUpdatedAt()).isNotNull();
    }

    @Test
    void onUpdate_shouldSetUpdatedAt() {
        UUID id = UUID.randomUUID();
        User user = new User(id, "admin", "pass", Role.USER);
        user.onCreate();

        user.onUpdate();

        assertThat(user.getUpdatedAt()).isNotNull();
    }
}
