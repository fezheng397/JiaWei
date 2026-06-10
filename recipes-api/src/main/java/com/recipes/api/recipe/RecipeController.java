package com.recipes.api.recipe;

import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.dto.RecipeIngredientRedirectResponse;
import com.recipes.api.recipe.dto.RecipeResponse;
import com.recipes.api.recipe.dto.RecipeSummaryResponse;
import com.recipes.api.recipe.entity.RecipeKind;
import jakarta.validation.Valid;
import java.net.URI;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(value = "/recipes", produces = MediaType.APPLICATION_JSON_VALUE)
public class RecipeController {
  private final RecipeService recipeService;

  public RecipeController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @GetMapping
  public List<RecipeSummaryResponse> getRecipes(@RequestParam Optional<String> kind) {
    return recipeService.getRecipes(kind.map(this::parseKind));
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public ResponseEntity<RecipeResponse> createRecipe(
      @Valid @RequestBody RecipeCreateRequest request) {
    RecipeResponse createdRecipe = recipeService.createRecipe(request);

    return ResponseEntity.created(URI.create("/recipes/" + createdRecipe.publicId()))
        .body(createdRecipe);
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
