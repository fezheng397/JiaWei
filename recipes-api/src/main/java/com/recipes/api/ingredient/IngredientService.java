package com.recipes.api.ingredient;

import com.recipes.api.common.ConflictException;
import com.recipes.api.common.NotFoundException;
import com.recipes.api.ingredient.dto.IngredientResponse;
import com.recipes.api.ingredient.entity.Ingredient;
import com.recipes.api.ingredient.repository.IngredientRepository;
import com.recipes.api.recipe.RecipeMapper;
import com.recipes.api.recipe.dto.RecipeSummaryResponse;
import com.recipes.api.recipe.entity.Recipe;
import com.recipes.api.recipe.entity.RecipeKind;
import com.recipes.api.recipe.repository.RecipeRepository;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IngredientService {
  private final IngredientRepository ingredientRepository;
  private final RecipeRepository recipeRepository;
  private final RecipeMapper recipeMapper;

  public IngredientService(
      IngredientRepository ingredientRepository,
      RecipeRepository recipeRepository,
      RecipeMapper recipeMapper) {
    this.ingredientRepository = ingredientRepository;
    this.recipeRepository = recipeRepository;
    this.recipeMapper = recipeMapper;
  }

  public List<IngredientResponse> getIngredients(boolean hasRecipe) {
    List<Ingredient> ingredients =
        hasRecipe
            ? ingredientRepository.findAllHavingRecipeOrderByName()
            : ingredientRepository.findAllByOrderByName();

    return ingredients.stream().map(this::toIngredient).toList();
  }

  public IngredientResponse getIngredient(String ingredientPublicId) {
    Ingredient ingredient =
        ingredientRepository
            .findByPublicId(ingredientPublicId)
            .orElseThrow(
                () -> new NotFoundException("Ingredient not found: " + ingredientPublicId));

    return toIngredient(ingredient);
  }

  @Transactional
  public IngredientResponse createIngredient(String name) {
    String normalizedName = IngredientNameNormalizer.normalize(name);

    ingredientRepository
        .findByNameIgnoringCase(normalizedName)
        .ifPresent(this::throwIngredientAlreadyExists);

    try {
      return toIngredient(ingredientRepository.saveAndFlush(new Ingredient(normalizedName)));
    } catch (DataIntegrityViolationException exception) {
      throw new ConflictException("Ingredient already exists: " + normalizedName);
    }
  }

  private IngredientResponse toIngredient(Ingredient ingredient) {
    RecipeSummaryResponse madeByRecipe =
        recipeRepository
            .findByKindAndIngredient(RecipeKind.INGREDIENT, ingredient)
            .map(recipeMapper::toSummary)
            .orElse(null);

    List<RecipeSummaryResponse> usedInRecipes =
        recipeRepository.findUsedByIngredient(ingredient).stream()
            .limit(5)
            .map(recipeMapper::toSummary)
            .toList();

    return new IngredientResponse(
        ingredient.getPublicId(), ingredient.getName(), madeByRecipe, usedInRecipes);
  }

  private void throwIngredientAlreadyExists(Ingredient ingredient) {
    throw new ConflictException(
        "Ingredient already exists: "
            + ingredient.getName()
            + " (publicId: "
            + ingredient.getPublicId()
            + ")");
  }
}
