package com.recipes.api.media.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class MediaAssetTests {
  @Test
  void advancesStatusThroughDomainMethods() {
    MediaAsset asset =
        new MediaAsset(
            MediaType.IMAGE,
            "recipes/status-test.jpg",
            "https://media.jiawei.app/recipes/status-test.jpg",
            null,
            "image/jpeg",
            4096);

    assertEquals(MediaAssetStatus.PENDING_UPLOAD, asset.getStatus());

    asset.markUploaded();
    assertEquals(MediaAssetStatus.UPLOADED, asset.getStatus());

    asset.markDeleted();
    assertEquals(MediaAssetStatus.DELETED, asset.getStatus());
  }
}
