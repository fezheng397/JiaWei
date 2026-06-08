package com.recipes.api.ingredient.dto;

import com.recipes.api.recipe.dto.RecipeSummaryResponse;
import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record IngredientResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String publicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String name,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true)
        RecipeSummaryResponse madeByRecipe,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<RecipeSummaryResponse> usedInRecipes) {}
