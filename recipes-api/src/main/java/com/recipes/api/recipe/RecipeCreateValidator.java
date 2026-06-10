package com.recipes.api.recipe;

import com.recipes.api.author.entity.Author;
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
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.Map;
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

  RecipeCreateReferences validate(RecipeCreateRequest request) {
    RecipeKind kind = RecipeKind.valueOf(request.kind().toUpperCase(Locale.ROOT));

    Ingredient producedIngredient = validateKindSpecificFields(request, kind);
    Author author = validateAuthor(request.authorPublicId());

    return validateIngredientLines(request, author, producedIngredient);
  }

  private Ingredient validateKindSpecificFields(RecipeCreateRequest request, RecipeKind kind) {
    if (kind == RecipeKind.DISH) {
      requireAbsent(request.producedIngredientPublicId(), "producedIngredientPublicId");
      requireAbsent(request.yieldQuantity(), "yieldQuantity");
      requireAbsent(request.yieldUnit(), "yieldUnit");
      return null;
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

    return producedIngredient;
  }

  private Author validateAuthor(String authorPublicId) {
    return authorRepository
        .findByPublicId(authorPublicId)
        .orElseThrow(() -> new NotFoundException("Author not found: " + authorPublicId));
  }

  private RecipeCreateReferences validateIngredientLines(
      RecipeCreateRequest request, Author author, Ingredient producedIngredient) {
    Set<String> clientRefs = new HashSet<>();
    Map<String, Ingredient> ingredientsByClientRef = new LinkedHashMap<>();
    Map<String, Recipe> preparedRecipesByClientRef = new LinkedHashMap<>();

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

      ingredientsByClientRef.put(ingredientLine.clientRef(), ingredient);

      Recipe preparedByRecipe = validatePreparedByRecipe(ingredientLine, ingredient);
      if (preparedByRecipe != null) {
        preparedRecipesByClientRef.put(ingredientLine.clientRef(), preparedByRecipe);
      }
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

    return new RecipeCreateReferences(
        author,
        producedIngredient,
        Map.copyOf(ingredientsByClientRef),
        Map.copyOf(preparedRecipesByClientRef));
  }

  private Recipe validatePreparedByRecipe(
      RecipeIngredientCreateRequest ingredientLine, Ingredient ingredient) {
    if (!hasText(ingredientLine.preparedByRecipePublicId())) {
      return null;
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

    return preparedByRecipe;
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
