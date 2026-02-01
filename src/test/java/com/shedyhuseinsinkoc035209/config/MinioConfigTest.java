package com.shedyhuseinsinkoc035209.config;

import io.minio.MinioClient;
import org.junit.jupiter.api.Test;
import org.springframework.test.util.ReflectionTestUtils;

import static org.assertj.core.api.Assertions.assertThat;

class MinioConfigTest {

    @Test
    void minioClient_shouldCreateClientWithProperties() {
        MinioConfig config = new MinioConfig();
        ReflectionTestUtils.setField(config, "endpoint", "http://localhost:9000");
        ReflectionTestUtils.setField(config, "accessKey", "minioadmin");
        ReflectionTestUtils.setField(config, "secretKey", "minioadmin");

        MinioClient client = config.minioClient();

        assertThat(client).isNotNull();
    }
}
