package com.recipes.api.recipe;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.List;

public record RecipeWriteRequest(
    @NotBlank String kind,
    @NotBlank String name,
    @NotBlank String description,
    @Valid @NotNull AuthorReferenceRequest author,
    String category,
    List<String> tags,
    String heroImageUrl,
    Integer prepTimeMinutes,
    Integer cookTimeMinutes,
    String difficulty,
    Integer servings,
    String yieldQuantity,
    String yieldUnit,
    @Valid IngredientReferenceRequest producedIngredient,
    @Valid @NotNull List<RecipeIngredientWriteRequest> ingredients,
    @Valid @NotNull List<RecipeStepWriteRequest> steps) {}
