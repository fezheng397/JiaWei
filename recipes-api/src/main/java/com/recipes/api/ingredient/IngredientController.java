package com.recipes.api.ingredient;

import com.recipes.api.recipe.RecipeService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
  private final RecipeService recipeService;

  public IngredientController(RecipeService recipeService) {
    this.recipeService = recipeService;
  }

  @GetMapping
  public List<IngredientResponse> getIngredients(
      @RequestParam(defaultValue = "false") boolean hasRecipe) {
    return recipeService.getIngredients(hasRecipe);
  }

  @GetMapping("/{publicId}")
  public IngredientResponse getIngredient(@PathVariable String publicId) {
    return recipeService.getIngredient(publicId);
  }
}
