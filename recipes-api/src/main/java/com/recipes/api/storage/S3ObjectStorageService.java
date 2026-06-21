package com.recipes.api.storage;

import com.recipes.api.config.StorageProperties;
import java.net.URI;
import java.time.Duration;
import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Configuration;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.PresignedPutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.model.PutObjectPresignRequest;

@Service
public class S3ObjectStorageService implements ObjectStorageService, AutoCloseable {
  private final StorageProperties properties;
  private final S3Presigner presigner;

  public S3ObjectStorageService(StorageProperties properties) {
    this.properties = properties;
    this.presigner =
        S3Presigner.builder()
            .endpointOverride(URI.create(properties.endpoint()))
            .region(Region.of(properties.region()))
            .credentialsProvider(
                StaticCredentialsProvider.create(
                    AwsBasicCredentials.create(
                        properties.accessKeyId(), properties.secretAccessKey())))
            .serviceConfiguration(S3Configuration.builder().pathStyleAccessEnabled(true).build())
            .build();
  }

  @Override
  public PresignedUpload createPresignedUpload(
      String objectKey, String contentType, Duration expiresIn) {
    PutObjectRequest putObjectRequest =
        PutObjectRequest.builder()
            .bucket(properties.bucket())
            .key(objectKey)
            .contentType(contentType)
            .build();
    PresignedPutObjectRequest presignedRequest =
        presigner.presignPutObject(
            PutObjectPresignRequest.builder()
                .signatureDuration(expiresIn)
                .putObjectRequest(putObjectRequest)
                .build());
    Map<String, String> requiredHeaders = new LinkedHashMap<>();
    presignedRequest.signedHeaders().forEach(
        (name, values) -> {
          if (!name.equalsIgnoreCase("host")) {
            String responseName = name.equalsIgnoreCase("content-type") ? "Content-Type" : name;
            requiredHeaders.put(responseName, String.join(",", values));
          }
        });

    return new PresignedUpload(
        presignedRequest.url().toExternalForm(),
        presignedRequest.httpRequest().method().name(),
        Map.copyOf(requiredHeaders),
        Instant.now().plus(expiresIn));
  }

  @Override
  public void close() {
    presigner.close();
  }
}
