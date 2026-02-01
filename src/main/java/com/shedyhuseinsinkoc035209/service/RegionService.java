package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.dto.RegionExternalDto;
import com.shedyhuseinsinkoc035209.entity.Region;
import com.shedyhuseinsinkoc035209.exception.ExternalApiException;
import com.shedyhuseinsinkoc035209.repository.RegionRepository;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegionService {

    private final RegionRepository regionRepository;
    private final RestClient restClient;

    private static final String EXTERNAL_API_URL = "https://integrador-argus-api.geia.vip/v1/regionais";

    public RegionService(RegionRepository regionRepository) {
        this.regionRepository = regionRepository;
        this.restClient = RestClient.create();
    }

    public List<Region> findAll() {
        return regionRepository.findAll();
    }

    public List<Region> findAllActive() {
        return regionRepository.findByActiveTrue();
    }

    @Transactional
    public List<Region> synchronize() {
        List<RegionExternalDto> externalRegions = restClient.get()
                .uri(EXTERNAL_API_URL)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RegionExternalDto>>() {});

        if (externalRegions == null) {
            throw new ExternalApiException("Failed to fetch regions from external API");
        }

        Map<Integer, Region> activeRegionsMap = regionRepository.findByActiveTrue().stream()
                .collect(Collectors.toMap(Region::getExternalId, r -> r));

        Set<Integer> externalIds = externalRegions.stream()
                .map(RegionExternalDto::getId)
                .collect(Collectors.toSet());

        // Process external regions
        for (RegionExternalDto dto : externalRegions) {
            Region existing = activeRegionsMap.get(dto.getId());

            if (existing == null) {
                // New region - insert
                Region newRegion = new Region(dto.getId(), dto.getNome(), true);
                regionRepository.save(newRegion);
            } else if (existing.hasNameChanged(dto.getNome())) {
                // Name changed - deactivate old and insert new (different surrogate PKs)
                existing.deactivate();
                regionRepository.save(existing);

                Region updated = new Region(dto.getId(), dto.getNome(), true);
                regionRepository.save(updated);
            }
        }

        // Deactivate active regions missing from external API
        for (Map.Entry<Integer, Region> entry : activeRegionsMap.entrySet()) {
            if (!externalIds.contains(entry.getKey())) {
                Region region = entry.getValue();
                region.deactivate();
                regionRepository.save(region);
            }
        }

        return regionRepository.findByActiveTrue();
    }
}
