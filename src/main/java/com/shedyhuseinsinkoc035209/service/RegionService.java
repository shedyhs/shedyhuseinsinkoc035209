package com.shedyhuseinsinkoc035209.service;

import com.shedyhuseinsinkoc035209.client.RegionExternalClient;
import com.shedyhuseinsinkoc035209.dto.RegionExternalDto;
import com.shedyhuseinsinkoc035209.entity.Region;
import com.shedyhuseinsinkoc035209.repository.RegionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RegionService {

    private static final Logger LOG = LoggerFactory.getLogger(RegionService.class);

    private final RegionRepository regionRepository;
    private final RegionExternalClient regionExternalClient;

    public RegionService(RegionRepository regionRepository, RegionExternalClient regionExternalClient) {
        this.regionRepository = regionRepository;
        this.regionExternalClient = regionExternalClient;
    }

    public List<Region> findAll() {
        return regionRepository.findAll();
    }

    public List<Region> findAllActive() {
        return regionRepository.findByActiveTrue();
    }

    @Transactional
    public List<Region> synchronize() {
        LOG.info("Synchronizing regions with external API");
        List<RegionExternalDto> externalRegions = regionExternalClient.fetchRegions();

        Map<Integer, Region> activeRegionsMap = regionRepository.findByActiveTrue().stream()
                .collect(Collectors.toMap(Region::getExternalId, r -> r));

        Set<Integer> externalIds = externalRegions.stream()
                .map(RegionExternalDto::id)
                .collect(Collectors.toSet());

        // Process external regions
        for (RegionExternalDto dto : externalRegions) {
            Region existing = activeRegionsMap.get(dto.id());

            if (existing == null) {
                // New region - insert
                Region newRegion = new Region(dto.id(), dto.nome(), true);
                regionRepository.save(newRegion);
            } else if (existing.hasNameChanged(dto.nome())) {
                // Name changed - deactivate old and insert new (different surrogate PKs)
                existing.deactivate();
                regionRepository.save(existing);

                Region updated = new Region(dto.id(), dto.nome(), true);
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

        List<Region> activeRegions = regionRepository.findByActiveTrue();
        LOG.info("Region synchronization completed. Active regions: {}", activeRegions.size());
        return activeRegions;
    }
}
