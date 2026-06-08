package com.recipes.api.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.List;

public record StepResponse(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String publicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) int position,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String instructions,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, nullable = true) Integer timerMinutes,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) String usedIngredients,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        List<IngredientLineResponse> ingredientDetails) {}
