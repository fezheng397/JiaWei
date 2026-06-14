package com.recipes.api.media.repository;

import com.recipes.api.media.entity.MediaAsset;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MediaAssetRepository extends JpaRepository<MediaAsset, UUID> {
  Optional<MediaAsset> findByPublicId(String publicId);

  Optional<MediaAsset> findByObjectKey(String objectKey);
}
