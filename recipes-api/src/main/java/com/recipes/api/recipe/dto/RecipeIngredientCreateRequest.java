package com.recipes.api.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;

public record RecipeIngredientCreateRequest(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String clientRef,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String ingredientPublicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String quantity,
    String unit,
    String preparedByRecipePublicId) {}
