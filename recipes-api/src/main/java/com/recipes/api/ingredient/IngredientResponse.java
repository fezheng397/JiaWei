package com.recipes.api.ingredient;

import com.recipes.api.recipe.RecipeSummaryResponse;
import java.util.List;

public record IngredientResponse(
    String publicId,
    String name,
    RecipeSummaryResponse madeByRecipe,
    List<RecipeSummaryResponse> usedInRecipes) {}
