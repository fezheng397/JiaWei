package com.recipes.api.recipe;

import java.util.List;

public record StepResponse(
    String publicId,
    int position,
    String instructions,
    Integer timerMinutes,
    String usedIngredients,
    List<IngredientLineResponse> ingredientDetails) {}
