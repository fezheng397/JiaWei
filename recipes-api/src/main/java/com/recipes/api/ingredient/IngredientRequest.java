package com.recipes.api.ingredient;

import jakarta.validation.constraints.NotBlank;

public record IngredientRequest(@NotBlank String name) {}
