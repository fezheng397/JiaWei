package com.recipes.api.ingredient;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class IngredientNameNormalizerTests {
  @Test
  void normalizesWhitespaceAndCasing() {
    assertThat(IngredientNameNormalizer.normalize("  soy sauce  ")).isEqualTo("Soy Sauce");
    assertThat(IngredientNameNormalizer.normalize("SOY SAUCE")).isEqualTo("Soy Sauce");
    assertThat(IngredientNameNormalizer.normalize("green   onion")).isEqualTo("Green Onion");
  }
}
