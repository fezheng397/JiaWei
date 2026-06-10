package com.recipes.api.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import java.util.List;

public record RecipeStepCreateRequest(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String instructions,
    @Positive Integer timerMinutes,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        List<@NotBlank String> ingredientLineRefs) {}
