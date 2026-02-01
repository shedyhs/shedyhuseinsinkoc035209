package com.shedyhuseinsinkoc035209.client;

import com.shedyhuseinsinkoc035209.exception.ExternalApiException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

class RegionExternalClientImplTest {

    @Test
    void fetchRegions_shouldThrowWhenApiUrlIsInvalid() {
        RegionExternalClientImpl client = new RegionExternalClientImpl("http://invalid-host-that-does-not-exist:9999/api");

        assertThatThrownBy(client::fetchRegions)
                .isInstanceOf(Exception.class);
    }

    @Test
    void constructor_shouldCreateClientWithUrl() {
        RegionExternalClientImpl client = new RegionExternalClientImpl("http://localhost:8080/api");

        assertThatThrownBy(client::fetchRegions)
                .isInstanceOf(Exception.class);
    }
}
