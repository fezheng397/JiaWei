package com.recipes.api.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipes.api.config.StorageProperties;
import com.recipes.api.media.dto.MediaUploadRequest;
import com.recipes.api.media.dto.MediaUploadResponse;
import com.recipes.api.media.entity.MediaAsset;
import com.recipes.api.media.entity.MediaAssetStatus;
import com.recipes.api.media.entity.MediaType;
import com.recipes.api.media.repository.MediaAssetRepository;
import com.recipes.api.storage.ObjectStorageService;
import com.recipes.api.storage.PresignedUpload;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

class MediaUploadServiceTests {
  private static final long MAX_UPLOAD_BYTES = 5_000_000;
  private static final StorageProperties STORAGE_PROPERTIES =
      new StorageProperties(
          "images",
          "https://r2.example.com",
          "auto",
          "access-key",
          "secret-key",
          "https://media.jiawei.app/",
          10,
          MAX_UPLOAD_BYTES);

  private final MediaAssetRepository mediaAssetRepository = mock(MediaAssetRepository.class);
  private final ObjectStorageService objectStorageService = mock(ObjectStorageService.class);
  private final MediaUploadService service =
      new MediaUploadService(mediaAssetRepository, objectStorageService, STORAGE_PROPERTIES);

  @Test
  void rejectsUnsupportedContentType() {
    MediaUploadRequest request = new MediaUploadRequest("photo.gif", "image/gif", 1024);

    IllegalArgumentException error =
        assertThrows(IllegalArgumentException.class, () -> service.createUpload(request));

    assertEquals(
        "Unsupported content type: 'image/gif'. Must be one of: image/jpeg, image/png, image/webp",
        error.getMessage());
    verify(mediaAssetRepository, never()).save(any());
    verify(objectStorageService, never()).createPresignedUpload(any(), any(), any());
  }

  @Test
  void rejectsOversizedUpload() {
    MediaUploadRequest request =
        new MediaUploadRequest("photo.jpg", "image/jpeg", MAX_UPLOAD_BYTES + 1);

    IllegalArgumentException error =
        assertThrows(IllegalArgumentException.class, () -> service.createUpload(request));

    assertEquals(
        "Upload exceeds the maximum size of 5000000 bytes",
        error.getMessage());
    verify(mediaAssetRepository, never()).save(any());
    verify(objectStorageService, never()).createPresignedUpload(any(), any(), any());
  }

  @Test
  void createsPendingMediaAssetAndReturnsPresignedUploadInstructions() {
    Instant expiresAt = Instant.parse("2026-06-14T16:30:00Z");
    when(objectStorageService.createPresignedUpload(any(), any(), any()))
        .thenReturn(
            new PresignedUpload(
                "https://upload.example.com/signed",
                "PUT",
                Map.of("content-type", "image/webp"),
                expiresAt));

    MediaUploadResponse response =
        service.createUpload(new MediaUploadRequest(" dinner.webp ", " IMAGE/WEBP ", 2048));

    ArgumentCaptor<MediaAsset> assetCaptor = ArgumentCaptor.forClass(MediaAsset.class);
    verify(mediaAssetRepository).save(assetCaptor.capture());
    MediaAsset asset = assetCaptor.getValue();
    assertNotNull(asset.getPublicId());
    assertEquals(MediaType.IMAGE, asset.getMediaType());
    assertEquals(MediaAssetStatus.PENDING_UPLOAD, asset.getStatus());
    assertEquals("dinner.webp", asset.getOriginalFilename());
    assertEquals("image/webp", asset.getContentType());
    assertEquals(2048L, asset.getSizeBytes());
    assertEquals(
        "media/tmp/%s/original.webp".formatted(asset.getPublicId()), asset.getObjectKey());
    assertEquals("https://media.jiawei.app/" + asset.getObjectKey(), asset.getPublicUrl());

    verify(objectStorageService)
        .createPresignedUpload(asset.getObjectKey(), "image/webp", Duration.ofMinutes(10));
    assertEquals(asset.getPublicId(), response.mediaPublicId());
    assertEquals(asset.getObjectKey(), response.objectKey());
    assertEquals("https://upload.example.com/signed", response.uploadUrl());
    assertEquals(asset.getPublicUrl(), response.publicUrl());
    assertEquals("PUT", response.method());
    assertEquals(Map.of("content-type", "image/webp"), response.headers());
    assertEquals(expiresAt.toString(), response.expiresAt());
  }
}
