package com.recipes.api.recipe;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RecipeCreateValidatorTests {
  private final AuthorRepository authorRepository = mock(AuthorRepository.class);
  private final IngredientRepository ingredientRepository = mock(IngredientRepository.class);
  private final RecipeRepository recipeRepository = mock(RecipeRepository.class);
  private final RecipeCreateValidator validator =
      new RecipeCreateValidator(authorRepository, ingredientRepository, recipeRepository);

  @BeforeEach
  void setUpValidReferences() {
    Ingredient ingredient = ingredient("ingredient-id");
    Ingredient producedIngredient = ingredient("produced-ingredient-id");

    when(authorRepository.findByPublicId("author-id")).thenReturn(Optional.of(mock(Author.class)));
    when(ingredientRepository.findByPublicId("ingredient-id"))
        .thenReturn(Optional.of(ingredient));
    when(ingredientRepository.findByPublicId("produced-ingredient-id"))
        .thenReturn(Optional.of(producedIngredient));
  }

  @Test
  void acceptsValidDishRecipe() {
    assertDoesNotThrow(() -> validator.validate(dishRequest()));
  }

  @Test
  void acceptsValidIngredientRecipe() {
    assertDoesNotThrow(() -> validator.validate(ingredientRequest()));
  }

  @Test
  void rejectsDishRecipeWithIngredientRecipeFields() {
    RecipeCreateRequest request =
        copyDish(
            "produced-ingredient-id",
            null,
            null,
            dishRequest().ingredients(),
            dishRequest().steps());

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));

    assertEquals(
        "producedIngredientPublicId must be omitted for dish recipes", exception.getMessage());
  }

  @Test
  void rejectsIngredientRecipeWithoutRequiredYield() {
    RecipeCreateRequest request =
        copyIngredient(null, ingredientRequest().ingredients(), ingredientRequest().steps());

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));

    assertEquals("yieldQuantity is required for ingredient recipes", exception.getMessage());
  }

  @Test
  void rejectsMissingReferencedResources() {
    when(authorRepository.findByPublicId("author-id")).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> validator.validate(dishRequest()));

    assertEquals("Author not found: author-id", exception.getMessage());
  }

  @Test
  void rejectsMissingIngredient() {
    when(ingredientRepository.findByPublicId("ingredient-id")).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> validator.validate(dishRequest()));

    assertEquals("Ingredient not found: ingredient-id", exception.getMessage());
  }

  @Test
  void rejectsIngredientThatAlreadyHasAnIngredientRecipe() {
    Recipe existingRecipe = mock(Recipe.class);
    Ingredient producedIngredient =
        ingredientRepository.findByPublicId("produced-ingredient-id").orElseThrow();
    when(existingRecipe.getPublicId()).thenReturn("existing-recipe-id");
    when(recipeRepository.findByKindAndIngredient(RecipeKind.INGREDIENT, producedIngredient))
        .thenReturn(Optional.of(existingRecipe));

    ConflictException exception =
        assertThrows(ConflictException.class, () -> validator.validate(ingredientRequest()));

    assertEquals(
        "Ingredient already has a recipe: produced-ingredient-id (recipe publicId: existing-recipe-id)",
        exception.getMessage());
  }

  @Test
  void rejectsDuplicateIngredientClientRefs() {
    RecipeIngredientCreateRequest duplicate =
        new RecipeIngredientCreateRequest("line-1", "ingredient-id", "2", null, null);
    RecipeCreateRequest request =
        copyDish(
            null,
            null,
            null,
            List.of(dishRequest().ingredients().getFirst(), duplicate),
            dishRequest().steps());

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));

    assertEquals("Ingredient clientRef must be unique: line-1", exception.getMessage());
  }

  @Test
  void rejectsStepReferencesThatDoNotBelongToSubmittedIngredients() {
    RecipeStepCreateRequest step =
        new RecipeStepCreateRequest("Cook it.", null, List.of("unknown-line"));
    RecipeCreateRequest request =
        copyDish(null, null, null, dishRequest().ingredients(), List.of(step));

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));

    assertEquals(
        "Step references unknown ingredient clientRef: unknown-line", exception.getMessage());
  }

  @Test
  void rejectsDuplicateIngredientReferencesWithinStep() {
    RecipeStepCreateRequest step =
        new RecipeStepCreateRequest("Cook it.", null, List.of("line-1", "line-1"));
    RecipeCreateRequest request =
        copyDish(null, null, null, dishRequest().ingredients(), List.of(step));

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));

    assertEquals(
        "Step references ingredient clientRef more than once: line-1", exception.getMessage());
  }

  @Test
  void rejectsPreparedByRecipeForDifferentIngredient() {
    Recipe preparedByRecipe = mock(Recipe.class);
    Ingredient differentIngredient = ingredient("different-ingredient-id");
    when(preparedByRecipe.getKind()).thenReturn(RecipeKind.INGREDIENT);
    when(preparedByRecipe.getIngredient()).thenReturn(differentIngredient);
    when(recipeRepository.findByPublicId("prepared-recipe-id"))
        .thenReturn(Optional.of(preparedByRecipe));
    RecipeIngredientCreateRequest line =
        new RecipeIngredientCreateRequest(
            "line-1", "ingredient-id", "1", null, "prepared-recipe-id");
    RecipeCreateRequest request =
        copyDish(null, null, null, List.of(line), dishRequest().steps());

    IllegalArgumentException exception =
        assertThrows(IllegalArgumentException.class, () -> validator.validate(request));

    assertEquals(
        "preparedByRecipePublicId must reference an ingredient recipe for ingredientPublicId: ingredient-id",
        exception.getMessage());
  }

  @Test
  void rejectsMissingPreparedByRecipe() {
    RecipeIngredientCreateRequest line =
        new RecipeIngredientCreateRequest(
            "line-1", "ingredient-id", "1", null, "missing-recipe-id");
    RecipeCreateRequest request =
        copyDish(null, null, null, List.of(line), dishRequest().steps());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> validator.validate(request));

    assertEquals("Recipe not found: missing-recipe-id", exception.getMessage());
  }

  private RecipeCreateRequest dishRequest() {
    return copyDish(
        null,
        null,
        null,
        List.of(new RecipeIngredientCreateRequest("line-1", "ingredient-id", "1", null, null)),
        List.of(new RecipeStepCreateRequest("Cook it.", null, List.of("line-1"))));
  }

  private RecipeCreateRequest ingredientRequest() {
    return new RecipeCreateRequest(
        "ingredient",
        "Prepared Ingredient",
        "Description",
        "author-id",
        List.of("Prep"),
        null,
        null,
        null,
        null,
        null,
        "produced-ingredient-id",
        "2",
        "cups",
        dishRequest().ingredients(),
        dishRequest().steps());
  }

  private RecipeCreateRequest copyDish(
      String producedIngredientPublicId,
      String yieldQuantity,
      String yieldUnit,
      List<RecipeIngredientCreateRequest> ingredients,
      List<RecipeStepCreateRequest> steps) {
    return new RecipeCreateRequest(
        "dish",
        "Dish",
        "Description",
        "author-id",
        List.of("Dinner"),
        null,
        null,
        null,
        null,
        4,
        producedIngredientPublicId,
        yieldQuantity,
        yieldUnit,
        ingredients,
        steps);
  }

  private RecipeCreateRequest copyIngredient(
      String yieldQuantity,
      List<RecipeIngredientCreateRequest> ingredients,
      List<RecipeStepCreateRequest> steps) {
    return new RecipeCreateRequest(
        "ingredient",
        "Prepared Ingredient",
        "Description",
        "author-id",
        List.of("Prep"),
        null,
        null,
        null,
        null,
        null,
        "produced-ingredient-id",
        yieldQuantity,
        "cups",
        ingredients,
        steps);
  }

  private Ingredient ingredient(String publicId) {
    Ingredient ingredient = mock(Ingredient.class);
    when(ingredient.getPublicId()).thenReturn(publicId);
    return ingredient;
  }
}
