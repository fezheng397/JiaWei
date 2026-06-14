package com.recipes.api.media;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.recipes.api.media.entity.MediaAsset;
import com.recipes.api.media.entity.MediaType;
import com.recipes.api.media.repository.MediaAssetRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class MediaAssetPersistenceTests {
  @Autowired private MediaAssetRepository mediaAssetRepository;

  @Test
  void persistsAndFindsMediaAssetByPublicIdAndObjectKey() {
    MediaAsset asset =
        mediaAssetRepository.saveAndFlush(
            new MediaAsset(
                MediaType.IMAGE,
                "recipes/test-image.jpg",
                "https://media.jiawei.app/recipes/test-image.jpg",
                "test-image.jpg",
                "image/jpeg",
                2048));

    assertNotNull(asset.getPublicId());
    assertEquals(
        asset.getId(), mediaAssetRepository.findByPublicId(asset.getPublicId()).orElseThrow().getId());
    assertEquals(
        asset.getId(),
        mediaAssetRepository.findByObjectKey(asset.getObjectKey()).orElseThrow().getId());
  }
}
