package com.recipes.api.recipe;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RecipeStepWriteRequest(
    @NotBlank String instructions,
    Integer timerMinutes,
    @NotNull List<String> ingredientClientIds) {}
