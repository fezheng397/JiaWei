package com.recipes.api.common;

import jakarta.persistence.Column;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;
import java.time.OffsetDateTime;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;

@MappedSuperclass
public abstract class AuditedEntity {
  @Id
  @GeneratedValue
  @UuidGenerator
  @Column(nullable = false, updatable = false, columnDefinition = "uuid")
  private UUID id;

  @Column(name = "public_id", nullable = false, unique = true, length = 26)
  private String publicId;

  @Column(name = "created_at", nullable = false, updatable = false)
  private OffsetDateTime createdAt;

  @Column(name = "updated_at", nullable = false)
  private OffsetDateTime updatedAt;

  @Column(name = "created_by", columnDefinition = "uuid")
  private UUID createdBy;

  @Column(name = "updated_by", columnDefinition = "uuid")
  private UUID updatedBy;

  @Version
  @Column(nullable = false)
  private Integer version;

  public UUID getId() {
    return id;
  }

  public String getPublicId() {
    return publicId;
  }

  public OffsetDateTime getCreatedAt() {
    return createdAt;
  }

  public OffsetDateTime getUpdatedAt() {
    return updatedAt;
  }

  public UUID getCreatedBy() {
    return createdBy;
  }

  public UUID getUpdatedBy() {
    return updatedBy;
  }

  public Integer getVersion() {
    return version;
  }

  protected final String initializePublicId() {
    if (publicId == null) {
      publicId = PublicIdGenerator.generate();
    }
    return publicId;
  }

  @PrePersist
  void prePersist() {
    OffsetDateTime now = OffsetDateTime.now();

    initializePublicId();

    createdAt = now;
    updatedAt = now;

    if (version == null) {
      version = 0;
    }
  }

  @PreUpdate
  void preUpdate() {
    updatedAt = OffsetDateTime.now();
  }
}
