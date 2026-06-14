package com.recipes.api.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipes.api.common.NotFoundException;
import com.recipes.api.media.repository.MediaAssetRepository;
import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.repository.RecipeIngredientRepository;
import com.recipes.api.recipe.repository.RecipeRepository;
import com.recipes.api.recipe.repository.RecipeStepRepository;
import com.recipes.api.recipe.repository.StepIngredientRepository;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;

class RecipeServiceTests {
  @Test
  void createRecipeStopsBeforePersistenceWhenValidationFails() {
    RecipeCreateValidator validator = mock(RecipeCreateValidator.class);
    RecipeRepository recipeRepository = mock(RecipeRepository.class);
    RecipeService service =
        new RecipeService(
            recipeRepository,
            mock(RecipeIngredientRepository.class),
            mock(RecipeStepRepository.class),
            mock(StepIngredientRepository.class),
            mock(MediaAssetRepository.class),
            mock(RecipeMapper.class),
            validator);
    RecipeCreateRequest request =
        new RecipeCreateRequest(
            "dish",
            "Dish",
            "Description",
            "author-id",
            List.of(),
            null,
            null,
            null,
            null,
            4,
            null,
            null,
            null,
            List.of(),
            List.of());
    when(validator.validate(request)).thenThrow(new IllegalArgumentException("Invalid recipe"));

    assertThrows(IllegalArgumentException.class, () -> service.createRecipe(request));

    verify(validator).validate(request);
    verify(recipeRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
  }

  @Test
  void createRecipeRejectsUnknownHeroImage() {
    RecipeCreateValidator validator = mock(RecipeCreateValidator.class);
    RecipeRepository recipeRepository = mock(RecipeRepository.class);
    MediaAssetRepository mediaAssetRepository = mock(MediaAssetRepository.class);
    RecipeService service =
        new RecipeService(
            recipeRepository,
            mock(RecipeIngredientRepository.class),
            mock(RecipeStepRepository.class),
            mock(StepIngredientRepository.class),
            mediaAssetRepository,
            mock(RecipeMapper.class),
            validator);
    RecipeCreateRequest request =
        new RecipeCreateRequest(
            "dish",
            "Dish",
            "Description",
            "author-id",
            List.of("test"),
            null,
            "missing-media-id",
            null,
            null,
            null,
            4,
            null,
            null,
            null,
            List.of(),
            List.of());
    when(validator.validate(request)).thenReturn(mock(RecipeCreateReferences.class));
    when(mediaAssetRepository.findByPublicId("missing-media-id")).thenReturn(Optional.empty());

    NotFoundException exception =
        assertThrows(NotFoundException.class, () -> service.createRecipe(request));

    assertEquals("Media asset not found: missing-media-id", exception.getMessage());
    verify(recipeRepository, never()).saveAndFlush(org.mockito.ArgumentMatchers.any());
  }
}
