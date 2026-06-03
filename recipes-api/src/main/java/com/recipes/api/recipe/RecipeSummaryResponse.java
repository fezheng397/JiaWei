package com.recipes.api.recipe;

import java.util.List;

public record RecipeSummaryResponse(
    String publicId,
    String name,
    String description,
    String categoryLabel,
    String authorName,
    String publishedAt,
    String kind,
    String ingredientPublicId,
    String heroImageUrl,
    Integer totalTimeMinutes,
    String difficulty,
    List<String> tags,
    Integer servings,
    String yieldAmount) {}
