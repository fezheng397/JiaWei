package com.recipes.api.author.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record AuthorResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String publicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name) {}
