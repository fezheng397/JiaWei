package com.recipes.api.recipe;

import com.recipes.api.recipe.dto.IngredientLineResponse;
import com.recipes.api.recipe.dto.RecipeResponse;
import com.recipes.api.recipe.dto.RecipeSummaryResponse;
import com.recipes.api.recipe.dto.StepResponse;
import com.recipes.api.recipe.entity.Recipe;
import com.recipes.api.recipe.entity.RecipeIngredient;
import com.recipes.api.recipe.entity.RecipeKind;
import com.recipes.api.recipe.entity.RecipeStep;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Component;

@Component
public class RecipeMapper {
  public RecipeResponse toResponse(
      Recipe recipe, List<IngredientLineResponse> ingredients, List<StepResponse> steps) {
    return new RecipeResponse(
        recipe.getPublicId(),
        recipe.getName(),
        recipe.getDescription(),
        categoryLabelFor(recipe),
        recipe.getAuthor().getName(),
        recipe.getPublishedAt() == null ? null : recipe.getPublishedAt().toString(),
        recipe.getKind().toApiValue(),
        recipe.getIngredient() == null ? null : recipe.getIngredient().getPublicId(),
        recipe.getHeroImageUrl(),
        recipe.getPrepTimeMinutes(),
        recipe.getCookTimeMinutes(),
        totalTimeFor(recipe),
        recipe.getDifficulty() == null ? null : recipe.getDifficulty().toApiValue(),
        recipe.getTags(),
        recipe.getServings(),
        yieldAmountFor(recipe),
        ingredients,
        steps);
  }

  public RecipeSummaryResponse toSummary(Recipe recipe) {
    return new RecipeSummaryResponse(
        recipe.getPublicId(),
        recipe.getName(),
        recipe.getDescription(),
        categoryLabelFor(recipe),
        recipe.getAuthor().getName(),
        recipe.getPublishedAt() == null ? null : recipe.getPublishedAt().toString(),
        recipe.getKind().toApiValue(),
        recipe.getIngredient() == null ? null : recipe.getIngredient().getPublicId(),
        recipe.getHeroImageUrl(),
        totalTimeFor(recipe),
        recipe.getDifficulty() == null ? null : recipe.getDifficulty().toApiValue(),
        recipe.getTags(),
        recipe.getServings(),
        yieldAmountFor(recipe));
  }

  public StepResponse toStep(RecipeStep step, List<IngredientLineResponse> ingredientDetails) {
    String usedIngredients =
        ingredientDetails.stream()
            .map(IngredientLineResponse::name)
            .collect(Collectors.joining(", "));

    return new StepResponse(
        step.getPublicId(),
        step.getPosition(),
        step.getInstructions(),
        step.getTimerMinutes(),
        usedIngredients,
        ingredientDetails);
  }

  public IngredientLineResponse toIngredientLine(RecipeIngredient recipeIngredient) {
    Recipe preparedByRecipe = recipeIngredient.getPreparedByRecipe();

    return new IngredientLineResponse(
        recipeIngredient.getPublicId(),
        recipeIngredient.getIngredient().getPublicId(),
        amountFor(recipeIngredient),
        recipeIngredient.getIngredient().getName(),
        preparedByRecipe == null ? null : preparedByRecipe.getPublicId(),
        preparedByRecipe == null ? null : preparedByRecipe.getName());
  }

  private String amountFor(RecipeIngredient ingredient) {
    return ingredient.getUnit() == null
        ? ingredient.getQuantity()
        : ingredient.getQuantity() + " " + ingredient.getUnit();
  }

  private Integer totalTimeFor(Recipe recipe) {
    Integer prep = recipe.getPrepTimeMinutes();
    Integer cook = recipe.getCookTimeMinutes();

    if (prep == null) {
      return cook;
    }

    if (cook == null) {
      return prep;
    }

    return prep + cook;
  }

  private String yieldAmountFor(Recipe recipe) {
    if (recipe.getKind() != RecipeKind.INGREDIENT) {
      return null;
    }

    return recipe.getYieldUnit() == null
        ? recipe.getYieldQuantity()
        : recipe.getYieldQuantity() + " " + recipe.getYieldUnit();
  }

  private String categoryLabelFor(Recipe recipe) {
    if (!recipe.getTags().isEmpty()) {
      return recipe.getTags().getFirst();
    }

    return recipe.getKind() == RecipeKind.DISH ? "Recipe" : "Ingredient";
  }
}
