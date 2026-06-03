package com.recipes.api.common;

import java.time.Instant;

public record ApiError(String message, Instant timestamp) {
  public static ApiError now(String message) {
    return new ApiError(message, Instant.now());
  }
}
