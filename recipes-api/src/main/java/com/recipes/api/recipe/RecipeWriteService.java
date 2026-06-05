package com.recipes.api.recipe;

import com.recipes.api.author.Author;
import com.recipes.api.author.AuthorService;
import com.recipes.api.common.NotFoundException;
import com.recipes.api.ingredient.Ingredient;
import com.recipes.api.ingredient.IngredientService;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RecipeWriteService {
  private final AuthorService authorService;
  private final IngredientService ingredientService;
  private final RecipeRepository recipeRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final RecipeStepRepository recipeStepRepository;
  private final StepIngredientRepository stepIngredientRepository;
  private final RecipeService recipeService;

  public RecipeWriteService(
      AuthorService authorService,
      IngredientService ingredientService,
      RecipeRepository recipeRepository,
      RecipeIngredientRepository recipeIngredientRepository,
      RecipeStepRepository recipeStepRepository,
      StepIngredientRepository stepIngredientRepository,
      RecipeService recipeService) {
    this.authorService = authorService;
    this.ingredientService = ingredientService;
    this.recipeRepository = recipeRepository;
    this.recipeIngredientRepository = recipeIngredientRepository;
    this.recipeStepRepository = recipeStepRepository;
    this.stepIngredientRepository = stepIngredientRepository;
    this.recipeService = recipeService;
  }

  @Transactional
  public RecipeResponse createRecipe(RecipeWriteRequest request) {
    Recipe recipe = buildRecipe(null, request);
    recipeRepository.save(recipe);
    replaceChildren(recipe, request);
    recipeRepository.flush();
    return recipeService.getRecipe(recipe.getPublicId());
  }

  @Transactional
  public RecipeResponse updateRecipe(String publicId, RecipeWriteRequest request) {
    Recipe recipe =
        recipeRepository
            .findByPublicId(publicId)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + publicId));

    buildRecipe(recipe, request);
    stepIngredientRepository.deleteByStepRecipe(recipe);
    recipeStepRepository.deleteByRecipe(recipe);
    recipeIngredientRepository.deleteByRecipe(recipe);
    replaceChildren(recipe, request);
    recipeRepository.flush();
    return recipeService.getRecipe(recipe.getPublicId());
  }

  private Recipe buildRecipe(Recipe existingRecipe, RecipeWriteRequest request) {
    RecipeKind kind = parseKind(request.kind());
    Author author = resolveAuthor(request.author());
    Ingredient producedIngredient = resolveProducedIngredient(kind, request);
    String yieldQuantity =
        kind == RecipeKind.INGREDIENT ? requiredText(request.yieldQuantity(), "Yield") : null;
    String yieldUnit =
        kind == RecipeKind.INGREDIENT ? blankToNull(request.yieldUnit()) : null;
    Integer servings = kind == RecipeKind.DISH ? request.servings() : null;
    List<String> tags = normalizeTags(request.category(), request.tags());

    if (kind == RecipeKind.DISH && servings == null) {
      throw new IllegalArgumentException("Servings are required for dish recipes");
    }

    if (existingRecipe == null) {
      return Recipe.create(
          requiredText(request.name(), "Recipe title"),
          author,
          requiredText(request.description(), "Description"),
          blankToNull(request.heroImageUrl()),
          request.prepTimeMinutes(),
          request.cookTimeMinutes(),
          parseDifficulty(request.difficulty()),
          kind,
          producedIngredient,
          servings,
          yieldQuantity,
          yieldUnit,
          tags);
    }

    existingRecipe.update(
        requiredText(request.name(), "Recipe title"),
        author,
        requiredText(request.description(), "Description"),
        blankToNull(request.heroImageUrl()),
        request.prepTimeMinutes(),
        request.cookTimeMinutes(),
        parseDifficulty(request.difficulty()),
        kind,
        producedIngredient,
        servings,
        yieldQuantity,
        yieldUnit,
        tags);
    return existingRecipe;
  }

  private void replaceChildren(Recipe recipe, RecipeWriteRequest request) {
    Map<String, RecipeIngredient> ingredientsByClientId = new LinkedHashMap<>();
    List<RecipeIngredientWriteRequest> ingredientRequests =
        request.ingredients() == null ? List.of() : request.ingredients();
    Set<String> clientIds = new HashSet<>();

    for (int index = 0; index < ingredientRequests.size(); index++) {
      RecipeIngredientWriteRequest ingredientRequest = ingredientRequests.get(index);
      String clientId = requiredText(ingredientRequest.clientId(), "Ingredient clientId");
      if (!clientIds.add(clientId)) {
        throw new IllegalArgumentException("Duplicate ingredient clientId: " + clientId);
      }
      Ingredient ingredient = resolveIngredient(ingredientRequest.ingredient());
      Recipe preparedByRecipe =
          resolvePreparedByRecipe(ingredient, ingredientRequest.preparedByRecipePublicId());
      RecipeIngredient recipeIngredient =
          recipeIngredientRepository.save(
              RecipeIngredient.create(
                  recipe,
                  ingredient,
                  requiredText(ingredientRequest.quantity(), "Ingredient amount"),
                  blankToNull(ingredientRequest.unit()),
                  index + 1,
                  preparedByRecipe));
      ingredientsByClientId.put(clientId, recipeIngredient);
    }

    List<RecipeStepWriteRequest> stepRequests =
        request.steps() == null ? List.of() : request.steps();
    for (int index = 0; index < stepRequests.size(); index++) {
      RecipeStepWriteRequest stepRequest = stepRequests.get(index);
      RecipeStep step =
          recipeStepRepository.save(
              RecipeStep.create(
                  recipe,
                  index + 1,
                  requiredText(stepRequest.instructions(), "Step instructions"),
                  stepRequest.timerMinutes()));

      for (String ingredientClientId : safeList(stepRequest.ingredientClientIds())) {
        RecipeIngredient recipeIngredient = ingredientsByClientId.get(ingredientClientId);
        if (recipeIngredient == null) {
          throw new IllegalArgumentException(
              "Step references unknown ingredient line: " + ingredientClientId);
        }
        stepIngredientRepository.save(StepIngredient.create(step, recipeIngredient));
      }
    }
  }

  private Author resolveAuthor(AuthorReferenceRequest author) {
    if (author == null) {
      throw new IllegalArgumentException("Author is required");
    }

    return authorService.findOrCreate(author.publicId(), author.name());
  }

  private Ingredient resolveProducedIngredient(RecipeKind kind, RecipeWriteRequest request) {
    if (kind == RecipeKind.DISH) {
      return null;
    }

    IngredientReferenceRequest producedIngredient = request.producedIngredient();
    if (producedIngredient == null) {
      return ingredientService.findOrCreate(null, request.name());
    }

    return ingredientService.findOrCreate(
        producedIngredient.publicId(), producedIngredient.name());
  }

  private Ingredient resolveIngredient(IngredientReferenceRequest ingredient) {
    if (ingredient == null) {
      throw new IllegalArgumentException("Ingredient is required");
    }

    return ingredientService.findOrCreate(ingredient.publicId(), ingredient.name());
  }

  private Recipe resolvePreparedByRecipe(Ingredient ingredient, String preparedByRecipePublicId) {
    if (preparedByRecipePublicId == null || preparedByRecipePublicId.isBlank()) {
      return null;
    }

    return recipeRepository
        .findByPublicIdAndKindAndIngredient(
            preparedByRecipePublicId, RecipeKind.INGREDIENT, ingredient)
        .orElseThrow(
            () ->
                new IllegalArgumentException(
                    "Linked ingredient recipe does not produce " + ingredient.getName()));
  }

  private RecipeKind parseKind(String value) {
    String normalized = requiredText(value, "Recipe kind").toUpperCase(Locale.ROOT);
    try {
      return RecipeKind.valueOf(normalized);
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("Recipe kind must be dish or ingredient");
    }
  }

  private RecipeDifficulty parseDifficulty(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    try {
      return RecipeDifficulty.valueOf(value.trim().toUpperCase(Locale.ROOT));
    } catch (IllegalArgumentException exception) {
      throw new IllegalArgumentException("Difficulty must be easy, medium, or hard");
    }
  }

  private List<String> normalizeTags(String category, List<String> tags) {
    List<String> normalizedTags = new ArrayList<>();
    addTag(normalizedTags, category);
    safeList(tags).forEach(tag -> addTag(normalizedTags, tag));
    return normalizedTags;
  }

  private void addTag(List<String> tags, String tag) {
    String normalizedTag = blankToNull(tag);
    if (normalizedTag != null && !tags.contains(normalizedTag)) {
      tags.add(normalizedTag);
    }
  }

  private <T> List<T> safeList(List<T> values) {
    return values == null ? List.of() : values;
  }

  private String requiredText(String value, String label) {
    String normalized = blankToNull(value);
    if (normalized == null) {
      throw new IllegalArgumentException(label + " is required");
    }
    return normalized;
  }

  private String blankToNull(String value) {
    if (value == null || value.isBlank()) {
      return null;
    }

    return value.trim().replaceAll("\\s+", " ");
  }
}
