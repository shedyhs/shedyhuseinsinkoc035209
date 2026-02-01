package com.shedyhuseinsinkoc035209.client;

import com.shedyhuseinsinkoc035209.dto.RegionExternalDto;

import java.util.List;

public interface RegionExternalClient {

    List<RegionExternalDto> fetchRegions();
}
