package com.shedyhuseinsinkoc035209.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shedyhuseinsinkoc035209.dto.AlbumImageResponse;
import com.shedyhuseinsinkoc035209.service.AlbumImageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AlbumImageControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock
    private AlbumImageService albumImageService;

    @InjectMocks
    private AlbumImageController albumImageController;

    @BeforeEach
    void setUp() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(albumImageController)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void uploadImages_shouldReturn201() throws Exception {
        UUID albumId = UUID.randomUUID();
        AlbumImageResponse imageResponse = new AlbumImageResponse(
                UUID.randomUUID(), albumId, "test.jpg", "image/jpeg",
                "http://minio:9000/bucket/test.jpg", LocalDateTime.now());
        when(albumImageService.uploadImages(eq(albumId), any())).thenReturn(List.of(imageResponse));

        MockMultipartFile file = new MockMultipartFile("files", "test.jpg", "image/jpeg", "image-data".getBytes());

        mockMvc.perform(multipart("/api/v1/albums/{albumId}/images", albumId)
                        .file(file))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].fileName").value("test.jpg"));
    }

    @Test
    void getImagesByAlbumId_shouldReturn200() throws Exception {
        UUID albumId = UUID.randomUUID();
        AlbumImageResponse imageResponse = new AlbumImageResponse(
                UUID.randomUUID(), albumId, "test.jpg", "image/jpeg",
                "http://minio:9000/bucket/test.jpg", LocalDateTime.now());
        when(albumImageService.getImagesByAlbumId(albumId)).thenReturn(List.of(imageResponse));

        mockMvc.perform(get("/api/v1/albums/{albumId}/images", albumId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].fileName").value("test.jpg"));
    }

    @Test
    void deleteImage_shouldReturn204() throws Exception {
        UUID imageId = UUID.randomUUID();
        doNothing().when(albumImageService).deleteImage(imageId);

        mockMvc.perform(delete("/api/v1/albums/images/{imageId}", imageId))
                .andExpect(status().isNoContent());
    }
}
