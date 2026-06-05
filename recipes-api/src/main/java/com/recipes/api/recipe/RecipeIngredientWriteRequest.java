package com.recipes.api.recipe;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record RecipeIngredientWriteRequest(
    @NotBlank String clientId,
    @Valid @NotNull IngredientReferenceRequest ingredient,
    @NotBlank String quantity,
    String unit,
    String preparedByRecipePublicId) {}
