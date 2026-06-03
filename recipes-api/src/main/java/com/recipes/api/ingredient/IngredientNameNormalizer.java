package com.recipes.api.ingredient;

import java.util.Locale;

public final class IngredientNameNormalizer {
  private IngredientNameNormalizer() {}

  public static String normalize(String name) {
    if (name == null) {
      return null;
    }

    String collapsed = name.trim().replaceAll("\\s+", " ");

    if (collapsed.isEmpty()) {
      return collapsed;
    }

    String[] words = collapsed.toLowerCase(Locale.ROOT).split(" ");
    for (int index = 0; index < words.length; index++) {
      words[index] = Character.toUpperCase(words[index].charAt(0)) + words[index].substring(1);
    }

    return String.join(" ", words);
  }
}
