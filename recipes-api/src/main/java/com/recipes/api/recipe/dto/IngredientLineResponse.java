package com.recipes.api.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;

public record IngredientLineResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String publicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String ingredientPublicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String amount,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
        String preparedByRecipePublicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
        String preparedByRecipeName) {}
