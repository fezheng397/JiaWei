package com.recipes.api.recipe;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.repository.RecipeIngredientRepository;
import com.recipes.api.recipe.repository.RecipeRepository;
import com.recipes.api.recipe.repository.RecipeStepRepository;
import com.recipes.api.recipe.repository.StepIngredientRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class RecipeServiceTests {
  @Test
  void createRecipeValidatesBeforePersistenceIsImplemented() {
    RecipeCreateValidator validator = mock(RecipeCreateValidator.class);
    RecipeService service =
        new RecipeService(
            mock(RecipeRepository.class),
            mock(RecipeIngredientRepository.class),
            mock(RecipeStepRepository.class),
            mock(StepIngredientRepository.class),
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

    assertThrows(ResponseStatusException.class, () -> service.createRecipe(request));

    verify(validator).validate(request);
  }
}
