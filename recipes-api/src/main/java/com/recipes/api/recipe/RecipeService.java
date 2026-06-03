package com.recipes.api.recipe;

import com.recipes.api.common.ConflictException;
import com.recipes.api.common.NotFoundException;
import com.recipes.api.ingredient.Ingredient;
import com.recipes.api.ingredient.IngredientNameNormalizer;
import com.recipes.api.ingredient.IngredientRepository;
import com.recipes.api.ingredient.IngredientResponse;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RecipeService {
  private final IngredientRepository ingredientRepository;
  private final RecipeRepository recipeRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final RecipeStepRepository recipeStepRepository;
  private final StepIngredientRepository stepIngredientRepository;

  public RecipeService(
      IngredientRepository ingredientRepository,
      RecipeRepository recipeRepository,
      RecipeIngredientRepository recipeIngredientRepository,
      RecipeStepRepository recipeStepRepository,
      StepIngredientRepository stepIngredientRepository) {
    this.ingredientRepository = ingredientRepository;
    this.recipeRepository = recipeRepository;
    this.recipeIngredientRepository = recipeIngredientRepository;
    this.recipeStepRepository = recipeStepRepository;
    this.stepIngredientRepository = stepIngredientRepository;
  }

  public List<RecipeSummaryResponse> getRecipes(Optional<RecipeKind> kind) {
    return kind
        .map(recipeRepository::findByKindOrderByName)
        .orElseGet(recipeRepository::findAllByOrderByName)
        .stream()
        .map(this::toSummary)
        .toList();
  }

  public RecipeResponse getRecipe(String recipePublicId) {
    Recipe recipe =
        recipeRepository
            .findByPublicId(recipePublicId)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + recipePublicId));
    List<IngredientLineResponse> ingredients =
        recipeIngredientRepository.findByRecipeIdOrderByPosition(recipe.getId()).stream()
            .map(this::toIngredientLine)
            .toList();
    Map<String, List<IngredientLineResponse>> stepIngredients =
        stepIngredientRepository.findByRecipeId(recipe.getId()).stream()
            .collect(
                Collectors.groupingBy(
                    link -> link.getStep().getPublicId(),
                    Collectors.mapping(
                        link -> toIngredientLine(link.getRecipeIngredient()),
                        Collectors.toList())));
    List<StepResponse> steps =
        recipeStepRepository.findByRecipeIdOrderByPosition(recipe.getId()).stream()
            .map(step -> toStep(step, stepIngredients.getOrDefault(step.getPublicId(), List.of())))
            .toList();

    return new RecipeResponse(
        recipe.getPublicId(),
        recipe.getName(),
        recipe.getDescription(),
        categoryLabelFor(recipe),
        recipe.getAuthor().getName(),
        recipe.getPublishedAt() == null ? null : recipe.getPublishedAt().toString(),
        kindValue(recipe.getKind()),
        recipe.getIngredient() == null ? null : recipe.getIngredient().getPublicId(),
        recipe.getHeroImageUrl(),
        recipe.getPrepTimeMinutes(),
        recipe.getCookTimeMinutes(),
        totalTimeFor(recipe),
        difficultyValue(recipe.getDifficulty()),
        recipe.getTags(),
        recipe.getServings(),
        yieldAmountFor(recipe),
        ingredients,
        steps);
  }

  public List<IngredientResponse> getIngredients(boolean hasRecipe) {
    return ingredientRepository.findAllByOrderByName().stream()
        .map(this::toIngredient)
        .filter(ingredient -> !hasRecipe || ingredient.madeByRecipe() != null)
        .toList();
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

  public Optional<String> getIngredientRecipeRedirect(String recipePublicId) {
    return recipeRepository
        .findByPublicId(recipePublicId)
        .filter(recipe -> recipe.getKind() == RecipeKind.INGREDIENT)
        .map(Recipe::getIngredient)
        .filter(Objects::nonNull)
        .map(Ingredient::getPublicId);
  }

  private IngredientResponse toIngredient(Ingredient ingredient) {
    RecipeSummaryResponse madeByRecipe =
        recipeRepository
            .findByKindAndIngredientId(RecipeKind.INGREDIENT, ingredient.getId())
            .map(this::toSummary)
            .orElse(null);

    List<RecipeSummaryResponse> usedInRecipes =
        recipeIngredientRepository.findByIngredientId(ingredient.getId()).stream()
            .map(recipeIngredient -> recipeIngredient.getRecipe().getId())
            .distinct()
            .map(recipeRepository::findById)
            .flatMap(Optional::stream)
            .sorted(Comparator.comparing(Recipe::getName))
            .limit(5)
            .map(this::toSummary)
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

  private RecipeSummaryResponse toSummary(Recipe recipe) {
    return new RecipeSummaryResponse(
        recipe.getPublicId(),
        recipe.getName(),
        recipe.getDescription(),
        categoryLabelFor(recipe),
        recipe.getAuthor().getName(),
        recipe.getPublishedAt() == null ? null : recipe.getPublishedAt().toString(),
        kindValue(recipe.getKind()),
        recipe.getIngredient() == null ? null : recipe.getIngredient().getPublicId(),
        recipe.getHeroImageUrl(),
        totalTimeFor(recipe),
        difficultyValue(recipe.getDifficulty()),
        recipe.getTags(),
        recipe.getServings(),
        yieldAmountFor(recipe));
  }

  private StepResponse toStep(
      RecipeStep step, List<IngredientLineResponse> ingredientDetails) {
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

  private IngredientLineResponse toIngredientLine(RecipeIngredient recipeIngredient) {
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

  private String kindValue(RecipeKind kind) {
    return kind.name().toLowerCase(Locale.ROOT);
  }

  private String difficultyValue(RecipeDifficulty difficulty) {
    return difficulty == null ? null : difficulty.name().toLowerCase(Locale.ROOT);
  }
}
