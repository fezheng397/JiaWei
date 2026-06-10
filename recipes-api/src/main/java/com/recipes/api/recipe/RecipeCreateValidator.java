package com.recipes.api.recipe;

import com.recipes.api.author.repository.AuthorRepository;
import com.recipes.api.common.ConflictException;
import com.recipes.api.common.NotFoundException;
import com.recipes.api.ingredient.entity.Ingredient;
import com.recipes.api.ingredient.repository.IngredientRepository;
import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.dto.RecipeIngredientCreateRequest;
import com.recipes.api.recipe.dto.RecipeStepCreateRequest;
import com.recipes.api.recipe.entity.Recipe;
import com.recipes.api.recipe.entity.RecipeKind;
import com.recipes.api.recipe.repository.RecipeRepository;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import org.springframework.stereotype.Component;

@Component
class RecipeCreateValidator {
  private final AuthorRepository authorRepository;
  private final IngredientRepository ingredientRepository;
  private final RecipeRepository recipeRepository;

  RecipeCreateValidator(
      AuthorRepository authorRepository,
      IngredientRepository ingredientRepository,
      RecipeRepository recipeRepository) {
    this.authorRepository = authorRepository;
    this.ingredientRepository = ingredientRepository;
    this.recipeRepository = recipeRepository;
  }

  void validate(RecipeCreateRequest request) {
    RecipeKind kind = RecipeKind.valueOf(request.kind().toUpperCase(Locale.ROOT));

    validateKindSpecificFields(request, kind);
    validateAuthor(request.authorPublicId());
    validateIngredientLines(request);
  }

  private void validateKindSpecificFields(RecipeCreateRequest request, RecipeKind kind) {
    if (kind == RecipeKind.DISH) {
      requireAbsent(request.producedIngredientPublicId(), "producedIngredientPublicId");
      requireAbsent(request.yieldQuantity(), "yieldQuantity");
      requireAbsent(request.yieldUnit(), "yieldUnit");
      return;
    }

    requirePresent(request.producedIngredientPublicId(), "producedIngredientPublicId");
    requirePresent(request.yieldQuantity(), "yieldQuantity");

    if (request.servings() != null) {
      throw new IllegalArgumentException("servings must be omitted for ingredient recipes");
    }

    Ingredient producedIngredient =
        ingredientRepository
            .findByPublicId(request.producedIngredientPublicId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Ingredient not found: " + request.producedIngredientPublicId()));

    recipeRepository
        .findByKindAndIngredient(RecipeKind.INGREDIENT, producedIngredient)
        .ifPresent(
            existingRecipe -> {
              throw new ConflictException(
                  "Ingredient already has a recipe: "
                      + request.producedIngredientPublicId()
                      + " (recipe publicId: "
                      + existingRecipe.getPublicId()
                      + ")");
            });
  }

  private void validateAuthor(String authorPublicId) {
    authorRepository
        .findByPublicId(authorPublicId)
        .orElseThrow(() -> new NotFoundException("Author not found: " + authorPublicId));
  }

  private void validateIngredientLines(RecipeCreateRequest request) {
    Set<String> clientRefs = new HashSet<>();

    for (RecipeIngredientCreateRequest ingredientLine : request.ingredients()) {
      if (!clientRefs.add(ingredientLine.clientRef())) {
        throw new IllegalArgumentException(
            "Ingredient clientRef must be unique: " + ingredientLine.clientRef());
      }

      Ingredient ingredient =
          ingredientRepository
              .findByPublicId(ingredientLine.ingredientPublicId())
              .orElseThrow(
                  () ->
                      new NotFoundException(
                          "Ingredient not found: " + ingredientLine.ingredientPublicId()));

      validatePreparedByRecipe(ingredientLine, ingredient);
    }

    for (RecipeStepCreateRequest step : request.steps()) {
      Set<String> stepRefs = new HashSet<>();

      for (String ingredientLineRef : step.ingredientLineRefs()) {
        if (!clientRefs.contains(ingredientLineRef)) {
          throw new IllegalArgumentException(
              "Step references unknown ingredient clientRef: " + ingredientLineRef);
        }

        if (!stepRefs.add(ingredientLineRef)) {
          throw new IllegalArgumentException(
              "Step references ingredient clientRef more than once: " + ingredientLineRef);
        }
      }
    }
  }

  private void validatePreparedByRecipe(
      RecipeIngredientCreateRequest ingredientLine, Ingredient ingredient) {
    if (!hasText(ingredientLine.preparedByRecipePublicId())) {
      return;
    }

    Recipe preparedByRecipe =
        recipeRepository
            .findByPublicId(ingredientLine.preparedByRecipePublicId())
            .orElseThrow(
                () ->
                    new NotFoundException(
                        "Recipe not found: " + ingredientLine.preparedByRecipePublicId()));

    if (preparedByRecipe.getKind() != RecipeKind.INGREDIENT
        || preparedByRecipe.getIngredient() == null
        || !preparedByRecipe.getIngredient().getPublicId().equals(ingredient.getPublicId())) {
      throw new IllegalArgumentException(
          "preparedByRecipePublicId must reference an ingredient recipe for ingredientPublicId: "
              + ingredientLine.ingredientPublicId());
    }
  }

  private void requirePresent(String value, String field) {
    if (!hasText(value)) {
      throw new IllegalArgumentException(field + " is required for ingredient recipes");
    }
  }

  private void requireAbsent(String value, String field) {
    if (hasText(value)) {
      throw new IllegalArgumentException(field + " must be omitted for dish recipes");
    }
  }

  private boolean hasText(String value) {
    return value != null && !value.isBlank();
  }
}
