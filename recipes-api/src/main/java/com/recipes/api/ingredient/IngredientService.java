package com.recipes.api.ingredient;

import com.recipes.api.common.ConflictException;
import com.recipes.api.common.NotFoundException;
import com.recipes.api.recipe.Recipe;
import com.recipes.api.recipe.RecipeKind;
import com.recipes.api.recipe.RecipeRepository;
import com.recipes.api.recipe.RecipeService;
import java.util.List;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class IngredientService {
  private final IngredientRepository ingredientRepository;
  private final RecipeRepository recipeRepository;
  private final RecipeService recipeService;

  public IngredientService(
      IngredientRepository ingredientRepository,
      RecipeRepository recipeRepository,
      RecipeService recipeService) {
    this.ingredientRepository = ingredientRepository;
    this.recipeRepository = recipeRepository;
    this.recipeService = recipeService;
  }

  public List<IngredientResponse> getIngredients(boolean hasRecipe) {
    List<Ingredient> ingredients =
        hasRecipe
            ? ingredientRepository.findAllHavingRecipeOrderByName()
            : ingredientRepository.findAllByOrderByName();

    return ingredients.stream().map(this::toResponse).toList();
  }

  public IngredientResponse getIngredient(String publicId) {
    return toResponse(findByPublicId(publicId));
  }

  public Ingredient findByPublicId(String publicId) {
    return ingredientRepository
        .findByPublicId(publicId)
        .orElseThrow(() -> new NotFoundException("Ingredient not found: " + publicId));
  }

  @Transactional
  public Ingredient findOrCreate(String publicId, String name) {
    if (publicId != null && !publicId.isBlank()) {
      return findByPublicId(publicId);
    }

    String normalizedName = IngredientNameNormalizer.normalize(name);
    if (normalizedName == null || normalizedName.isBlank()) {
      throw new IllegalArgumentException("Ingredient name is required");
    }
    return ingredientRepository
        .findByNameIgnoringCase(normalizedName)
        .orElseGet(() -> ingredientRepository.save(new Ingredient(normalizedName)));
  }

  @Transactional
  public IngredientResponse createIngredient(String name) {
    String normalizedName = IngredientNameNormalizer.normalize(name);
    if (normalizedName == null || normalizedName.isBlank()) {
      throw new IllegalArgumentException("Ingredient name is required");
    }

    ingredientRepository
        .findByNameIgnoringCase(normalizedName)
        .ifPresent(this::throwIngredientAlreadyExists);

    try {
      return toResponse(ingredientRepository.saveAndFlush(new Ingredient(normalizedName)));
    } catch (DataIntegrityViolationException exception) {
      throw new ConflictException("Ingredient already exists: " + normalizedName);
    }
  }

  private IngredientResponse toResponse(Ingredient ingredient) {
    Recipe madeByRecipe =
        recipeRepository
            .findByKindAndIngredient(RecipeKind.INGREDIENT, ingredient)
            .orElse(null);

    return new IngredientResponse(
        ingredient.getPublicId(),
        ingredient.getName(),
        madeByRecipe == null ? null : recipeService.toSummary(madeByRecipe),
        recipeRepository.findUsedByIngredient(ingredient).stream()
            .limit(5)
            .map(recipeService::toSummary)
            .toList());
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
