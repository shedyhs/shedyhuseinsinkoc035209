package com.shedyhuseinsinkoc035209.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shedyhuseinsinkoc035209.dto.AlbumRequest;
import com.shedyhuseinsinkoc035209.dto.AlbumResponse;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import com.shedyhuseinsinkoc035209.service.AlbumService;
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
import org.springframework.data.web.PageableHandlerMethodArgumentResolver;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class AlbumControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock
    private AlbumService albumService;

    @InjectMocks
    private AlbumController albumController;

    @BeforeEach
    void setUp() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(albumController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(converter)
                .build();
    }

    private AlbumResponse createAlbumResponse() {
        return new AlbumResponse(
                UUID.randomUUID(),
                "Test Album",
                2023,
                List.of("Test Artist"),
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void create_shouldReturn201() throws Exception {
        AlbumRequest request = new AlbumRequest("Test Album", 2023, Set.of(UUID.randomUUID()));
        AlbumResponse response = createAlbumResponse();
        when(albumService.create(any(AlbumRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.title").value("Test Album"));
    }

    @Test
    void findById_shouldReturn200() throws Exception {
        AlbumResponse response = createAlbumResponse();
        when(albumService.findById(any(UUID.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/albums/{id}", response.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Test Album"));
    }

    @Test
    void findAll_shouldReturn200() throws Exception {
        AlbumResponse response = createAlbumResponse();
        Page<AlbumResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(albumService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/albums"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Album"));
    }

    @Test
    void findByArtistType_shouldReturn200() throws Exception {
        AlbumResponse response = createAlbumResponse();
        Page<AlbumResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(albumService.findByArtistType(eq(ArtistType.SOLO), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/albums/type/SOLO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Album"));
    }

    @Test
    void findByArtistName_shouldReturn200() throws Exception {
        AlbumResponse response = createAlbumResponse();
        Page<AlbumResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(albumService.findByArtistName(eq("Test"), eq("asc"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/albums/artist")
                        .param("name", "Test")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].title").value("Test Album"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        AlbumRequest request = new AlbumRequest("Updated Album", 2024, Set.of(UUID.randomUUID()));
        AlbumResponse response = new AlbumResponse(id, "Updated Album", 2024, List.of("Artist"),
                LocalDateTime.now(), LocalDateTime.now());
        when(albumService.update(eq(id), any(AlbumRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/albums/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.title").value("Updated Album"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(albumService).delete(id);

        mockMvc.perform(delete("/api/v1/albums/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void create_shouldReturn400ForInvalidRequest() throws Exception {
        AlbumRequest request = new AlbumRequest("", null, null);

        mockMvc.perform(post("/api/v1/albums")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
