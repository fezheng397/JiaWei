package com.recipes.api.media;

import com.recipes.api.config.StorageProperties;
import com.recipes.api.media.dto.MediaUploadRequest;
import com.recipes.api.media.dto.MediaUploadResponse;
import com.recipes.api.media.entity.MediaAsset;
import com.recipes.api.media.repository.MediaAssetRepository;
import com.recipes.api.storage.ObjectStorageService;
import com.recipes.api.storage.PresignedUpload;
import java.time.Duration;
import java.util.Locale;
import java.util.Map;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MediaUploadService {
  private static final Map<String, String> IMAGE_EXTENSIONS =
      Map.of("image/jpeg", "jpg", "image/png", "png", "image/webp", "webp");

  private final MediaAssetRepository mediaAssetRepository;
  private final ObjectStorageService objectStorageService;
  private final StorageProperties storageProperties;

  public MediaUploadService(
      MediaAssetRepository mediaAssetRepository,
      ObjectStorageService objectStorageService,
      StorageProperties storageProperties) {
    this.mediaAssetRepository = mediaAssetRepository;
    this.objectStorageService = objectStorageService;
    this.storageProperties = storageProperties;
  }

  @Transactional
  public MediaUploadResponse createUpload(MediaUploadRequest request) {
    String contentType = request.contentType().trim().toLowerCase(Locale.ROOT);
    String extension = IMAGE_EXTENSIONS.get(contentType);
    if (extension == null) {
      throw new IllegalArgumentException(
          "Unsupported content type: '%s'. Must be one of: image/jpeg, image/png, image/webp"
              .formatted(request.contentType()));
    }
    if (request.sizeBytes() <= 0) {
      throw new IllegalArgumentException("sizeBytes must be positive");
    }
    if (request.sizeBytes() > storageProperties.maxUploadBytes()) {
      throw new IllegalArgumentException(
          "Upload exceeds the maximum size of %d bytes"
              .formatted(storageProperties.maxUploadBytes()));
    }

    MediaAsset mediaAsset =
        MediaAsset.pendingImage(request.fileName().trim(), contentType, request.sizeBytes());
    String objectKey = "media/tmp/%s/original.%s".formatted(mediaAsset.getPublicId(), extension);
    String publicUrl = publicUrlFor(objectKey);
    mediaAsset.assignStorageLocation(objectKey, publicUrl);
    mediaAssetRepository.save(mediaAsset);

    Duration expiresIn = Duration.ofMinutes(storageProperties.presignExpirationMinutes());
    PresignedUpload upload =
        objectStorageService.createPresignedUpload(objectKey, contentType, expiresIn);

    return new MediaUploadResponse(
        mediaAsset.getPublicId(),
        objectKey,
        upload.uploadUrl(),
        publicUrl,
        upload.method(),
        upload.headers(),
        upload.expiresAt().toString());
  }

  private String publicUrlFor(String objectKey) {
    return storageProperties.publicBaseUrl().replaceAll("/+$", "") + "/" + objectKey;
  }
}
