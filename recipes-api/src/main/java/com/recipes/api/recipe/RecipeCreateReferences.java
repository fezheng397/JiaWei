package com.recipes.api.recipe;

import com.recipes.api.author.entity.Author;
import com.recipes.api.ingredient.entity.Ingredient;
import com.recipes.api.recipe.entity.Recipe;
import java.util.Map;

record RecipeCreateReferences(
    Author author,
    Ingredient producedIngredient,
    Map<String, Ingredient> ingredientsByClientRef,
    Map<String, Recipe> preparedRecipesByClientRef) {}
