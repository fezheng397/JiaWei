package com.recipes.api.recipe;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/recipes")
public class RecipeWriteController {
  private final RecipeWriteService recipeWriteService;

  public RecipeWriteController(RecipeWriteService recipeWriteService) {
    this.recipeWriteService = recipeWriteService;
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public RecipeResponse createRecipe(@Valid @RequestBody RecipeWriteRequest request) {
    return recipeWriteService.createRecipe(request);
  }

  @PutMapping("/{publicId}")
  public RecipeResponse updateRecipe(
      @PathVariable String publicId, @Valid @RequestBody RecipeWriteRequest request) {
    return recipeWriteService.updateRecipe(publicId, request);
  }
}
