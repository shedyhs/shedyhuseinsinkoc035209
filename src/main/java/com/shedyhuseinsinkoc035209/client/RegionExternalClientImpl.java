package com.shedyhuseinsinkoc035209.client;

import com.shedyhuseinsinkoc035209.dto.RegionExternalDto;
import com.shedyhuseinsinkoc035209.exception.ExternalApiException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class RegionExternalClientImpl implements RegionExternalClient {

    private final RestClient restClient;
    private final String externalApiUrl;

    public RegionExternalClientImpl(@Value("${region.external.api.url}") String externalApiUrl) {
        this.externalApiUrl = externalApiUrl;
        this.restClient = RestClient.create();
    }

    @Override
    public List<RegionExternalDto> fetchRegions() {
        List<RegionExternalDto> regions = restClient.get()
                .uri(externalApiUrl)
                .retrieve()
                .body(new ParameterizedTypeReference<List<RegionExternalDto>>() {});

        if (regions == null) {
            throw new ExternalApiException("Failed to fetch regions from external API");
        }

        return regions;
    }
}
