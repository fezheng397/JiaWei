package com.recipes.api.storage;

import java.time.Instant;
import java.util.Map;

public record PresignedUpload(
    String uploadUrl, String method, Map<String, String> headers, Instant expiresAt) {}
