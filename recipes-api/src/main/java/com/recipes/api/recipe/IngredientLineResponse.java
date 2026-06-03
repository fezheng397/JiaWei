package com.recipes.api.recipe;

public record IngredientLineResponse(
    String publicId,
    String ingredientPublicId,
    String amount,
    String name,
    String preparedByRecipePublicId,
    String preparedByRecipeName) {}
