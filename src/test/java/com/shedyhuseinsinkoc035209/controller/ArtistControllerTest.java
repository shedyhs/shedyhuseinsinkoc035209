package com.shedyhuseinsinkoc035209.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.shedyhuseinsinkoc035209.dto.ArtistRequest;
import com.shedyhuseinsinkoc035209.dto.ArtistResponse;
import com.shedyhuseinsinkoc035209.entity.ArtistType;
import com.shedyhuseinsinkoc035209.service.ArtistService;
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
import org.springframework.http.converter.json.JacksonJsonHttpMessageConverter;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class ArtistControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Mock
    private ArtistService artistService;

    @InjectMocks
    private ArtistController artistController;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(artistController)
                .setCustomArgumentResolvers(new PageableHandlerMethodArgumentResolver())
                .setMessageConverters(new JacksonJsonHttpMessageConverter())
                .build();
    }

    private ArtistResponse createArtistResponse() {
        return new ArtistResponse(
                UUID.randomUUID(),
                "Test Artist",
                ArtistType.SOLO,
                LocalDateTime.now(),
                LocalDateTime.now()
        );
    }

    @Test
    void create_shouldReturn201() throws Exception {
        ArtistRequest request = new ArtistRequest("Test Artist", ArtistType.SOLO);
        ArtistResponse response = createArtistResponse();
        when(artistService.create(any(ArtistRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/v1/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("Test Artist"));
    }

    @Test
    void findById_shouldReturn200() throws Exception {
        ArtistResponse response = createArtistResponse();
        when(artistService.findById(any(UUID.class))).thenReturn(response);

        mockMvc.perform(get("/api/v1/artists/{id}", response.id()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Test Artist"));
    }

    @Test
    void findAll_shouldReturn200() throws Exception {
        ArtistResponse response = createArtistResponse();
        Page<ArtistResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(artistService.findAll(any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/artists"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Artist"));
    }

    @Test
    void update_shouldReturn200() throws Exception {
        UUID id = UUID.randomUUID();
        ArtistRequest request = new ArtistRequest("Updated Artist", ArtistType.BAND);
        ArtistResponse response = new ArtistResponse(id, "Updated Artist", ArtistType.BAND,
                LocalDateTime.now(), LocalDateTime.now());
        when(artistService.update(eq(id), any(ArtistRequest.class))).thenReturn(response);

        mockMvc.perform(put("/api/v1/artists/{id}", id)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Artist"));
    }

    @Test
    void delete_shouldReturn204() throws Exception {
        UUID id = UUID.randomUUID();
        doNothing().when(artistService).delete(id);

        mockMvc.perform(delete("/api/v1/artists/{id}", id))
                .andExpect(status().isNoContent());
    }

    @Test
    void findByName_shouldReturn200() throws Exception {
        ArtistResponse response = createArtistResponse();
        Page<ArtistResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(artistService.findByName(eq("Test"), eq("asc"), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/artists/search")
                        .param("name", "Test")
                        .param("order", "asc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Artist"));
    }

    @Test
    void findByType_shouldReturn200() throws Exception {
        ArtistResponse response = createArtistResponse();
        Page<ArtistResponse> page = new PageImpl<>(List.of(response), PageRequest.of(0, 20), 1);
        when(artistService.findByType(eq(ArtistType.SOLO), any(Pageable.class))).thenReturn(page);

        mockMvc.perform(get("/api/v1/artists/type/{type}", "SOLO"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("Test Artist"));
    }

    @Test
    void create_shouldReturn400ForInvalidRequest() throws Exception {
        ArtistRequest request = new ArtistRequest("", null);

        mockMvc.perform(post("/api/v1/artists")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
