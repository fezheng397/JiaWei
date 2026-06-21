package com.recipes.api.config;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.storage")
public record StorageProperties(
    @NotBlank String bucket,
    @NotBlank String endpoint,
    @NotBlank String region,
    @NotBlank String accessKeyId,
    @NotBlank String secretAccessKey,
    @NotBlank String publicBaseUrl,
    @Positive long presignExpirationMinutes,
    @Positive long maxUploadBytes) {}
