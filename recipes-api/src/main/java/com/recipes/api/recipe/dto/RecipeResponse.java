package com.recipes.api.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record RecipeResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String publicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String description,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String categoryLabel,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String authorName,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true) String publishedAt,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"dish", "ingredient"})
        String kind,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
        String ingredientPublicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true) String heroImageUrl,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true) String heroImagePublicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true) Integer prepTimeMinutes,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true) Integer cookTimeMinutes,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true) Integer totalTimeMinutes,
    @Schema(
            requiredMode = Schema.RequiredMode.REQUIRED,
            nullable = true,
            allowableValues = {"easy", "medium", "hard"})
        String difficulty,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<String> tags,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true) Integer servings,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true) String yieldAmount,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<IngredientLineResponse> ingredients,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<StepResponse> steps) {
  public RecipeResponse(
      String publicId,
      String name,
      String description,
      String categoryLabel,
      String authorName,
      String publishedAt,
      String kind,
      String ingredientPublicId,
      String heroImageUrl,
      Integer prepTimeMinutes,
      Integer cookTimeMinutes,
      Integer totalTimeMinutes,
      String difficulty,
      List<String> tags,
      Integer servings,
      String yieldAmount,
      List<IngredientLineResponse> ingredients,
      List<StepResponse> steps) {
    this(
        publicId,
        name,
        description,
        categoryLabel,
        authorName,
        publishedAt,
        kind,
        ingredientPublicId,
        heroImageUrl,
        null,
        prepTimeMinutes,
        cookTimeMinutes,
        totalTimeMinutes,
        difficulty,
        tags,
        servings,
        yieldAmount,
        ingredients,
        steps);
  }
}
