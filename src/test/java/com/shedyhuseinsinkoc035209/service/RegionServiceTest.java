package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.client.RegionExternalClient;
import com.shedyhuseinsinkoc035209.dto.RegionExternalDto;
import com.shedyhuseinsinkoc035209.entity.Region;
import com.shedyhuseinsinkoc035209.exception.ExternalApiException;
import com.shedyhuseinsinkoc035209.repository.RegionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RegionServiceTest {

    @Mock
    private RegionRepository regionRepository;

    @Mock
    private RegionExternalClient regionExternalClient;

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

    @Test
    void synchronize_shouldInsertNewRegions() {
        RegionExternalDto externalDto = new RegionExternalDto(10, "Nordeste");
        when(regionExternalClient.fetchRegions()).thenReturn(List.of(externalDto));
        when(regionRepository.findByActiveTrue()).thenReturn(List.of());

        regionService.synchronize();

        verify(regionRepository).save(any(Region.class));
    }

    @Test
    void synchronize_shouldDeactivateRemovedRegions() {
        Region activeRegion = new Region(1, "Sul", true);
        when(regionExternalClient.fetchRegions()).thenReturn(List.of());
        when(regionRepository.findByActiveTrue()).thenReturn(List.of(activeRegion));

        regionService.synchronize();

        assertThat(activeRegion.getActive()).isFalse();
        verify(regionRepository).save(activeRegion);
    }

    @Test
    void synchronize_shouldHandleNameChange() {
        Region existingRegion = new Region(1, "Sul", true);
        RegionExternalDto externalDto = new RegionExternalDto(1, "Sul Atualizado");
        when(regionExternalClient.fetchRegions()).thenReturn(List.of(externalDto));
        when(regionRepository.findByActiveTrue()).thenReturn(List.of(existingRegion));

        regionService.synchronize();

        assertThat(existingRegion.getActive()).isFalse();
        verify(regionRepository, times(2)).save(any(Region.class));
    }

    @Test
    void synchronize_shouldDoNothingWhenRegionExistsWithSameName() {
        Region existingRegion = new Region(1, "Sul", true);
        RegionExternalDto externalDto = new RegionExternalDto(1, "Sul");
        when(regionExternalClient.fetchRegions()).thenReturn(List.of(externalDto));
        when(regionRepository.findByActiveTrue()).thenReturn(List.of(existingRegion));

        regionService.synchronize();

        assertThat(existingRegion.getActive()).isTrue();
        verify(regionRepository, never()).save(any(Region.class));
    }

    @Test
    void synchronize_shouldThrowWhenApiFails() {
        when(regionExternalClient.fetchRegions()).thenThrow(new ExternalApiException("Failed to fetch regions from external API"));

        assertThatThrownBy(() -> regionService.synchronize())
                .isInstanceOf(ExternalApiException.class)
                .hasMessage("Failed to fetch regions from external API");

        verify(regionRepository, never()).save(any());
    }
}
