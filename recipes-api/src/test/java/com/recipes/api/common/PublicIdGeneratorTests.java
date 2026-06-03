package com.recipes.api.common;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class PublicIdGeneratorTests {
  @Test
  void generatesUlidStylePublicIds() {
    String publicId = PublicIdGenerator.generate();

    assertThat(publicId).matches("^[0-9A-HJKMNP-TV-Z]{26}$");
  }
}
