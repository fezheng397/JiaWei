package com.recipes.api.common;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.Instant;

public record ApiError(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String message,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Instant timestamp) {
  public static ApiError now(String message) {
    return new ApiError(message, Instant.now());
  }
}
