package com.shedyhuseinsinkoc035209.controller;

import com.shedyhuseinsinkoc035209.dto.RegionResponse;
import com.shedyhuseinsinkoc035209.service.RegionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/regions")
@Tag(name = "Regionais", description = "Endpoints para gerenciamento de regionais")
public class RegionController {

    private final RegionService regionService;

    public RegionController(RegionService regionService) {
        this.regionService = regionService;
    }

    @GetMapping
    @Operation(summary = "Listar regionais", description = "Lista todas as regionais")
    public ResponseEntity<List<RegionResponse>> findAll() {
        List<RegionResponse> regions = regionService.findAll()
                .stream().map(RegionResponse::fromEntity).toList();
        return ResponseEntity.ok(regions);
    }

    @GetMapping("/active")
    @Operation(summary = "Listar regionais ativas", description = "Lista todas as regionais ativas")
    public ResponseEntity<List<RegionResponse>> findAllActive() {
        List<RegionResponse> regions = regionService.findAllActive()
                .stream().map(RegionResponse::fromEntity).toList();
        return ResponseEntity.ok(regions);
    }

    @PostMapping("/sync")
    @Operation(summary = "Sincronizar regionais", description = "Sincroniza regionais com a API externa")
    public ResponseEntity<List<RegionResponse>> synchronize() {
        List<RegionResponse> regions = regionService.synchronize()
                .stream().map(RegionResponse::fromEntity).toList();
        return ResponseEntity.ok(regions);
    }
}
