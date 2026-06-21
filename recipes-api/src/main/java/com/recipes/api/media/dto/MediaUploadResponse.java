package com.recipes.api.media.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.Map;

public record MediaUploadResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String mediaPublicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String objectKey,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String uploadUrl,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String publicUrl,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = "PUT") String method,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) Map<String, String> headers,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, format = "date-time") String expiresAt) {}
