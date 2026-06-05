package com.recipes.api.recipe;

import java.util.Locale;

public enum RecipeDifficulty {
  EASY,
  MEDIUM,
  HARD;

  public String toApiValue() {
    return name().toLowerCase(Locale.ROOT);
  }
}
