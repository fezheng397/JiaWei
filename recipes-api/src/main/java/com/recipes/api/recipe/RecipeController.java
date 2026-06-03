package com.recipes.api.recipe;

import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes")
public class RecipeController {
  private final RecipeService recipeService;

  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @GetMapping
  public List<RecipeSummaryResponse> getRecipes(@RequestParam Optional<String> kind) {
    return recipeService.getRecipes(kind.map(this::parseKind));
  }

  @GetMapping("/{publicId}")
  public RecipeResponse getRecipe(@PathVariable String publicId) {
    return recipeService.getRecipe(publicId);
  }

  @GetMapping("/{publicId}/ingredient-redirect")
  public ResponseEntity<RecipeIngredientRedirectResponse> getIngredientRedirect(
      @PathVariable String publicId) {
    return recipeService
        .getIngredientRecipeRedirect(publicId)
        .map(
            ingredientPublicId ->
                ResponseEntity.ok(new RecipeIngredientRedirectResponse(ingredientPublicId)))
        .orElseGet(() -> ResponseEntity.noContent().build());
  }

  private RecipeKind parseKind(String kind) {
    return RecipeKind.valueOf(kind.toUpperCase(Locale.ROOT));
  }
}
