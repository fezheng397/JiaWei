package com.recipes.api.media.entity;

import com.recipes.api.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;

@Entity
@Table(name = "media_assets")
public class MediaAsset extends AuditedEntity {
  @Enumerated(EnumType.STRING)
  @Column(name = "media_type", nullable = false, columnDefinition = "text")
  private MediaType mediaType;

  @Column(name = "object_key", nullable = false, unique = true, columnDefinition = "text")
  private String objectKey;

  @Column(name = "public_url", nullable = false, columnDefinition = "text")
  private String publicUrl;

  @Column(name = "original_filename", columnDefinition = "text")
  private String originalFilename;

  @Column(name = "content_type", nullable = false, columnDefinition = "text")
  private String contentType;

  @Column(name = "size_bytes", nullable = false)
  private Long sizeBytes;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, columnDefinition = "text")
  private MediaAssetStatus status;

  protected MediaAsset() {}

  public static MediaAsset pendingImage(
      String originalFilename, String contentType, long sizeBytes) {
    MediaAsset asset = new MediaAsset();
    asset.initializePublicId();
    asset.mediaType = MediaType.IMAGE;
    asset.originalFilename = originalFilename;
    asset.contentType = contentType;
    asset.sizeBytes = sizeBytes;
    asset.status = MediaAssetStatus.PENDING_UPLOAD;
    return asset;
  }

  public void assignStorageLocation(String objectKey, String publicUrl) {
    if (this.objectKey != null || this.publicUrl != null) {
      throw new IllegalStateException("Storage location is already assigned");
    }
    this.objectKey = objectKey;
    this.publicUrl = publicUrl;
  }

  public MediaAsset(
      MediaType mediaType,
      String objectKey,
      String publicUrl,
      String originalFilename,
      String contentType,
      long sizeBytes) {
    this.mediaType = mediaType;
    this.objectKey = objectKey;
    this.publicUrl = publicUrl;
    this.originalFilename = originalFilename;
    this.contentType = contentType;
    this.sizeBytes = sizeBytes;
    this.status = MediaAssetStatus.PENDING_UPLOAD;
  }

  public MediaType getMediaType() {
    return mediaType;
  }

  public String getObjectKey() {
    return objectKey;
  }

  public String getPublicUrl() {
    return publicUrl;
  }

  public String getOriginalFilename() {
    return originalFilename;
  }

  public String getContentType() {
    return contentType;
  }

  public Long getSizeBytes() {
    return sizeBytes;
  }

  public MediaAssetStatus getStatus() {
    return status;
  }

  public void markUploaded() {
    status = MediaAssetStatus.UPLOADED;
  }

  public void markDeleted() {
    status = MediaAssetStatus.DELETED;
  }
}
