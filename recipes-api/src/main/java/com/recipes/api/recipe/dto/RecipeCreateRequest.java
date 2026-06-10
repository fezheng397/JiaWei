package com.recipes.api.recipe.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import java.util.List;

public record RecipeCreateRequest(
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, allowableValues = {"dish", "ingredient"})
        @NotBlank
        @Pattern(regexp = "^(dish|ingredient)$")
        String kind,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String name,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String description,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED) @NotBlank String authorPublicId,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @NotNull
        List<@NotBlank String> tags,
    String heroImageUrl,
    @PositiveOrZero Integer prepTimeMinutes,
    @PositiveOrZero Integer cookTimeMinutes,
    @Schema(allowableValues = {"easy", "medium", "hard"})
        @Pattern(regexp = "^(easy|medium|hard)$")
        String difficulty,
    @Positive Integer servings,
    String producedIngredientPublicId,
    String yieldQuantity,
    String yieldUnit,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Valid
        @NotEmpty
        List<RecipeIngredientCreateRequest> ingredients,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
        @Valid
        @NotEmpty
        List<RecipeStepCreateRequest> steps) {}
