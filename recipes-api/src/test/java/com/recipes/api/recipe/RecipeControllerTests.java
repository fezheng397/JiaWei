package com.recipes.api.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.dto.RecipeResponse;
import java.net.URI;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

class RecipeControllerTests {
  @Test
  void createRecipeReturnsCreatedRecipeAndLocation() {
    RecipeService recipeService = mock(RecipeService.class);
    RecipeController controller = new RecipeController(recipeService);
    RecipeCreateRequest request =
        new RecipeCreateRequest(
            "dish",
            "Recipe",
            "Description",
            "author-id",
            List.of("Dinner"),
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
    RecipeResponse response =
        new RecipeResponse(
            "recipe-id",
            "Recipe",
            "Description",
            "Dinner",
            "Author",
            null,
            "dish",
            null,
            null,
            null,
            null,
            null,
            null,
            List.of("Dinner"),
            4,
            null,
            List.of(),
            List.of());
    when(recipeService.createRecipe(request)).thenReturn(response);

    ResponseEntity<RecipeResponse> result = controller.createRecipe(request);

    assertEquals(HttpStatus.CREATED, result.getStatusCode());
    assertEquals(URI.create("/recipes/recipe-id"), result.getHeaders().getLocation());
    assertSame(response, result.getBody());
    verify(recipeService).createRecipe(request);
  }
}
