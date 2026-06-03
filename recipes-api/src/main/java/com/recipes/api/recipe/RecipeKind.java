package com.recipes.api.recipe;

import java.util.Locale;

public enum RecipeKind {
  DISH,
  INGREDIENT;

  public String toApiValue() {
    return name().toLowerCase(Locale.ROOT);
  }
}
