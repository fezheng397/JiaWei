package com.recipes.api.ingredient.dto;

import jakarta.validation.constraints.NotBlank;

public record IngredientRequest(@NotBlank String name) {}
