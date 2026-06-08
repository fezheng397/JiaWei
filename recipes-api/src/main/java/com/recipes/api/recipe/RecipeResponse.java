package com.recipes.api.recipe;

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
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) List<StepResponse> steps) {}
