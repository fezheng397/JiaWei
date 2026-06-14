package com.recipes.api.recipe;

import com.recipes.api.common.NotFoundException;
import com.recipes.api.ingredient.entity.Ingredient;
import com.recipes.api.media.entity.MediaAsset;
import com.recipes.api.media.entity.MediaType;
import com.recipes.api.media.repository.MediaAssetRepository;
import com.recipes.api.recipe.dto.IngredientLineResponse;
import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.dto.RecipeIngredientCreateRequest;
import com.recipes.api.recipe.dto.RecipeResponse;
import com.recipes.api.recipe.dto.RecipeStepCreateRequest;
import com.recipes.api.recipe.dto.RecipeSummaryResponse;
import com.recipes.api.recipe.dto.StepResponse;
import com.recipes.api.recipe.entity.Recipe;
import com.recipes.api.recipe.entity.RecipeDifficulty;
import com.recipes.api.recipe.entity.RecipeIngredient;
import com.recipes.api.recipe.entity.RecipeKind;
import com.recipes.api.recipe.entity.RecipeStep;
import com.recipes.api.recipe.entity.StepIngredient;
import com.recipes.api.recipe.repository.RecipeIngredientRepository;
import com.recipes.api.recipe.repository.RecipeRepository;
import com.recipes.api.recipe.repository.RecipeStepRepository;
import com.recipes.api.recipe.repository.StepIngredientRepository;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class RecipeService {
  private final RecipeRepository recipeRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final RecipeStepRepository recipeStepRepository;
  private final StepIngredientRepository stepIngredientRepository;
  private final MediaAssetRepository mediaAssetRepository;
  private final RecipeMapper recipeMapper;
  private final RecipeCreateValidator recipeCreateValidator;

  public RecipeService(
      RecipeRepository recipeRepository,
      RecipeIngredientRepository recipeIngredientRepository,
      RecipeStepRepository recipeStepRepository,
      StepIngredientRepository stepIngredientRepository,
      MediaAssetRepository mediaAssetRepository,
      RecipeMapper recipeMapper,
      RecipeCreateValidator recipeCreateValidator) {
    this.recipeRepository = recipeRepository;
    this.recipeIngredientRepository = recipeIngredientRepository;
    this.recipeStepRepository = recipeStepRepository;
    this.stepIngredientRepository = stepIngredientRepository;
    this.mediaAssetRepository = mediaAssetRepository;
    this.recipeMapper = recipeMapper;
    this.recipeCreateValidator = recipeCreateValidator;
  }

  public List<RecipeSummaryResponse> getRecipes(Optional<RecipeKind> kind) {
    return kind
        .map(recipeRepository::findByKindOrderByName)
        .orElseGet(recipeRepository::findAllByOrderByName)
        .stream()
        .map(recipeMapper::toSummary)
        .toList();
  }

  @Transactional
  public RecipeResponse createRecipe(RecipeCreateRequest request) {
    RecipeCreateReferences references = recipeCreateValidator.validate(request);
    MediaAsset heroImage = resolveHeroImage(request.heroImagePublicId());
    Recipe recipe = toRecipe(request, references);
    recipe.assignHeroImage(heroImage);
    recipe = recipeRepository.saveAndFlush(recipe);
    Map<String, RecipeIngredient> ingredientLines =
        createIngredientLines(request, references, recipe);
    List<RecipeStep> steps = createSteps(request, recipe);

    createStepIngredients(request, ingredientLines, steps);

    return getRecipe(recipe.getPublicId());
  }

  public RecipeResponse getRecipe(String recipePublicId) {
    Recipe recipe =
        recipeRepository
            .findByPublicId(recipePublicId)
            .orElseThrow(() -> new NotFoundException("Recipe not found: " + recipePublicId));
    List<IngredientLineResponse> ingredients =
        recipeIngredientRepository.findByRecipeIdOrderByPosition(recipe.getId()).stream()
            .map(recipeMapper::toIngredientLine)
            .toList();
    Map<String, List<IngredientLineResponse>> stepIngredients =
        stepIngredientRepository.findByRecipeId(recipe.getId()).stream()
            .collect(
                Collectors.groupingBy(
                    link -> link.getStep().getPublicId(),
                    Collectors.mapping(
                        link -> recipeMapper.toIngredientLine(link.getRecipeIngredient()),
                        Collectors.toList())));
    List<StepResponse> steps =
        recipeStepRepository.findByRecipeIdOrderByPosition(recipe.getId()).stream()
            .map(
                step ->
                    recipeMapper.toStep(
                        step, stepIngredients.getOrDefault(step.getPublicId(), List.of())))
            .toList();

    return recipeMapper.toResponse(recipe, ingredients, steps);
  }

  public Optional<String> getIngredientRecipeRedirect(String recipePublicId) {
    return recipeRepository
        .findByPublicId(recipePublicId)
        .filter(recipe -> recipe.getKind() == RecipeKind.INGREDIENT)
        .map(Recipe::getIngredient)
        .filter(Objects::nonNull)
        .map(Ingredient::getPublicId);
  }

  private Recipe toRecipe(RecipeCreateRequest request, RecipeCreateReferences references) {
    RecipeDifficulty difficulty =
        request.difficulty() == null
            ? null
            : RecipeDifficulty.valueOf(request.difficulty().toUpperCase(Locale.ROOT));

    return new Recipe(
        request.name(),
        references.author(),
        request.description(),
        nullableText(request.heroImageUrl()),
        request.prepTimeMinutes(),
        request.cookTimeMinutes(),
        difficulty,
        RecipeKind.valueOf(request.kind().toUpperCase(Locale.ROOT)),
        references.producedIngredient(),
        request.servings(),
        nullableText(request.yieldQuantity()),
        nullableText(request.yieldUnit()),
        request.tags());
  }

  private MediaAsset resolveHeroImage(String heroImagePublicId) {
    String publicId = nullableText(heroImagePublicId);
    if (publicId == null) {
      return null;
    }

    MediaAsset heroImage =
        mediaAssetRepository
            .findByPublicId(publicId)
            .orElseThrow(() -> new NotFoundException("Media asset not found: " + publicId));

    if (heroImage.getMediaType() != MediaType.IMAGE) {
      throw new IllegalArgumentException("Hero image media asset must be an image: " + publicId);
    }

    return heroImage;
  }

  private Map<String, RecipeIngredient> createIngredientLines(
      RecipeCreateRequest request, RecipeCreateReferences references, Recipe recipe) {
    Map<String, RecipeIngredient> ingredientLines = new LinkedHashMap<>();

    for (int index = 0; index < request.ingredients().size(); index++) {
      RecipeIngredientCreateRequest ingredientRequest = request.ingredients().get(index);
      RecipeIngredient ingredientLine =
          new RecipeIngredient(
              recipe,
              references.ingredientsByClientRef().get(ingredientRequest.clientRef()),
              ingredientRequest.quantity(),
              nullableText(ingredientRequest.unit()),
              index + 1,
              references.preparedRecipesByClientRef().get(ingredientRequest.clientRef()));
      ingredientLines.put(ingredientRequest.clientRef(), ingredientLine);
    }

    recipeIngredientRepository.saveAllAndFlush(ingredientLines.values());
    return ingredientLines;
  }

  private List<RecipeStep> createSteps(RecipeCreateRequest request, Recipe recipe) {
    List<RecipeStep> steps = new ArrayList<>();

    for (int index = 0; index < request.steps().size(); index++) {
      RecipeStepCreateRequest stepRequest = request.steps().get(index);
      steps.add(
          new RecipeStep(recipe, index + 1, stepRequest.instructions(), stepRequest.timerMinutes()));
    }

    return recipeStepRepository.saveAllAndFlush(steps);
  }

  private void createStepIngredients(
      RecipeCreateRequest request,
      Map<String, RecipeIngredient> ingredientLines,
      List<RecipeStep> steps) {
    List<StepIngredient> stepIngredients = new ArrayList<>();

    for (int index = 0; index < request.steps().size(); index++) {
      RecipeStepCreateRequest stepRequest = request.steps().get(index);
      RecipeStep step = steps.get(index);

      stepRequest
          .ingredientLineRefs()
          .forEach(
              ingredientLineRef ->
                  stepIngredients.add(
                      new StepIngredient(step, ingredientLines.get(ingredientLineRef))));
    }

    stepIngredientRepository.saveAllAndFlush(stepIngredients);
  }

  private String nullableText(String value) {
    return value == null || value.isBlank() ? null : value;
  }
}
