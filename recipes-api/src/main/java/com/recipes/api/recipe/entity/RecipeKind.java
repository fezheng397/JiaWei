package com.recipes.api.recipe.entity;

import java.util.Locale;

public enum RecipeKind {
  DISH,
  INGREDIENT;

  public String toApiValue() {
    return name().toLowerCase(Locale.ROOT);
  }
}
