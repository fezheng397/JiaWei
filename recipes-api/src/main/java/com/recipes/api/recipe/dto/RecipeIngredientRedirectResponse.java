package com.recipes.api.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record RecipeIngredientRedirectResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String ingredientPublicId) {}
