package com.recipes.api.recipe;

import java.util.List;

public record RecipeResponse(
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
    List<StepResponse> steps) {}
