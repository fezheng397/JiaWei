package com.recipes.api.recipe;

import com.recipes.api.common.NotFoundException;
import com.recipes.api.ingredient.entity.Ingredient;
import com.recipes.api.recipe.dto.IngredientLineResponse;
import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.dto.RecipeResponse;
import com.recipes.api.recipe.dto.RecipeSummaryResponse;
import com.recipes.api.recipe.dto.StepResponse;
import com.recipes.api.recipe.entity.Recipe;
import com.recipes.api.recipe.entity.RecipeKind;
import com.recipes.api.recipe.repository.RecipeIngredientRepository;
import com.recipes.api.recipe.repository.RecipeRepository;
import com.recipes.api.recipe.repository.RecipeStepRepository;
import com.recipes.api.recipe.repository.StepIngredientRepository;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@Transactional(readOnly = true)
public class RecipeService {
  private final RecipeRepository recipeRepository;
  private final RecipeIngredientRepository recipeIngredientRepository;
  private final RecipeStepRepository recipeStepRepository;
  private final StepIngredientRepository stepIngredientRepository;
  private final RecipeMapper recipeMapper;

  public RecipeService(
      RecipeRepository recipeRepository,
      RecipeIngredientRepository recipeIngredientRepository,
      RecipeStepRepository recipeStepRepository,
      StepIngredientRepository stepIngredientRepository,
      RecipeMapper recipeMapper) {
    this.recipeRepository = recipeRepository;
    this.recipeIngredientRepository = recipeIngredientRepository;
    this.recipeStepRepository = recipeStepRepository;
    this.stepIngredientRepository = stepIngredientRepository;
    this.recipeMapper = recipeMapper;
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
    throw new ResponseStatusException(
        HttpStatus.NOT_IMPLEMENTED, "Recipe creation persistence is not implemented yet");
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
}
