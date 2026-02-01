package com.shedyhuseinsinkoc035209.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.shedyhuseinsinkoc035209.entity.Region;
import com.shedyhuseinsinkoc035209.service.RegionService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
class RegionControllerTest {

    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Mock
    private RegionService regionService;

    @InjectMocks
    private RegionController regionController;

    @BeforeEach
    void setUp() {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setObjectMapper(objectMapper);

        mockMvc = MockMvcBuilders.standaloneSetup(regionController)
                .setMessageConverters(converter)
                .build();
    }

    @Test
    void findAll_shouldReturn200() throws Exception {
        Region region = new Region(1, "Sul", true);
        when(regionService.findAll()).thenReturn(List.of(region));

        mockMvc.perform(get("/api/v1/regions"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sul"));
    }

    @Test
    void findAllActive_shouldReturn200() throws Exception {
        Region region = new Region(1, "Sul", true);
        when(regionService.findAllActive()).thenReturn(List.of(region));

        mockMvc.perform(get("/api/v1/regions/active"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sul"))
                .andExpect(jsonPath("$[0].active").value(true));
    }

    @Test
    void synchronize_shouldReturn200() throws Exception {
        Region region = new Region(1, "Sul", true);
        when(regionService.synchronize()).thenReturn(List.of(region));

        mockMvc.perform(post("/api/v1/regions/sync"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Sul"));
    }
}
