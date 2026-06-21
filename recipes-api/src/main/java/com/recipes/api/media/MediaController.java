package com.recipes.api.media;

import com.recipes.api.media.dto.MediaUploadRequest;
import com.recipes.api.media.dto.MediaUploadResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/media", produces = MediaType.APPLICATION_JSON_VALUE)
public class MediaController {
  private final MediaUploadService mediaUploadService;

  public MediaController(MediaUploadService mediaUploadService) {
    this.mediaUploadService = mediaUploadService;
  }

  @PostMapping("/uploads")
  @ResponseStatus(HttpStatus.CREATED)
  public MediaUploadResponse createUpload(@Valid @RequestBody MediaUploadRequest request) {
    return mediaUploadService.createUpload(request);
  }
}
