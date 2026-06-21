package com.recipes.api.storage;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.recipes.api.config.StorageProperties;
import java.time.Duration;
import org.junit.jupiter.api.Test;

class S3ObjectStorageServiceTests {
  @Test
  void presignsPutWithRequiredContentTypeHeader() {
    StorageProperties properties =
        new StorageProperties(
            "images",
            "https://account.r2.cloudflarestorage.com",
            "auto",
            "access-key",
            "secret-key",
            "https://media.jiawei.app",
            10,
            5_000_000);

    try (S3ObjectStorageService service = new S3ObjectStorageService(properties)) {
      PresignedUpload upload =
          service.createPresignedUpload(
              "media/tmp/01JTEST/original.webp", "image/webp", Duration.ofMinutes(10));

      assertEquals("PUT", upload.method());
      assertEquals("image/webp", upload.headers().get("Content-Type"));
      assertTrue(upload.uploadUrl().startsWith(properties.endpoint()));
    }
  }
}
