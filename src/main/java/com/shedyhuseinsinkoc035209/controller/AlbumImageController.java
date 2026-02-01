package com.shedyhuseinsinkoc035209.controller;

import com.shedyhuseinsinkoc035209.dto.AlbumImageResponse;
import com.shedyhuseinsinkoc035209.service.AlbumImageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/albums")
@Tag(name = "Imagens de Álbuns", description = "Endpoints para gerenciamento de imagens de álbuns")
public class AlbumImageController {

    private final AlbumImageService albumImageService;

    public AlbumImageController(AlbumImageService albumImageService) {
        this.albumImageService = albumImageService;
    }

    @PostMapping(value = "/{albumId}/images", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload de imagens", description = "Faz upload de imagens para um álbum")
    public ResponseEntity<List<AlbumImageResponse>> uploadImages(
            @PathVariable UUID albumId,
            @RequestParam("files") MultipartFile[] files) {
        List<AlbumImageResponse> responses = albumImageService.uploadImages(albumId, files);
        return ResponseEntity.status(HttpStatus.CREATED).body(responses);
    }

    @GetMapping("/{albumId}/images")
    @Operation(summary = "Listar imagens do álbum", description = "Lista todas as imagens de um álbum com URLs pré-assinadas")
    public ResponseEntity<List<AlbumImageResponse>> getImagesByAlbumId(@PathVariable UUID albumId) {
        List<AlbumImageResponse> responses = albumImageService.getImagesByAlbumId(albumId);
        return ResponseEntity.ok(responses);
    }

    @DeleteMapping("/images/{imageId}")
    @Operation(summary = "Excluir imagem", description = "Remove uma imagem do álbum e do MinIO")
    public ResponseEntity<Void> deleteImage(@PathVariable UUID imageId) {
        albumImageService.deleteImage(imageId);
        return ResponseEntity.noContent().build();
    }
}
