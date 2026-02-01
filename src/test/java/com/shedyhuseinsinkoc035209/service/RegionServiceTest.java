package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.entity.Region;
import com.shedyhuseinsinkoc035209.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @InjectMocks
    private RegionService regionService;

    @Test
    void findAll_shouldReturnAllRegions() {
        Region region1 = new Region(1, "Sul", true);
        Region region2 = new Region(2, "Norte", false);
        when(regionRepository.findAll()).thenReturn(List.of(region1, region2));

        List<Region> result = regionService.findAll();

        assertThat(result).hasSize(2);
        verify(regionRepository).findAll();
    }

    @Test
    void findAllActive_shouldReturnOnlyActiveRegions() {
        Region region = new Region(1, "Sul", true);
        when(regionRepository.findByActiveTrue()).thenReturn(List.of(region));

        List<Region> result = regionService.findAllActive();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getActive()).isTrue();
        verify(regionRepository).findByActiveTrue();
    }
}
