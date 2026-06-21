package com.recipes.api.storage;

import java.time.Duration;

public interface ObjectStorageService {
  PresignedUpload createPresignedUpload(
      String objectKey, String contentType, Duration expiresIn);
}
