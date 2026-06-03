package com.recipes.api.ingredient;

import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/ingredients")
public class IngredientController {
  private final IngredientService ingredientService;

  public IngredientController(IngredientService ingredientService) {
    this.ingredientService = ingredientService;
  }

  @GetMapping
  public List<IngredientResponse> getIngredients(
      @RequestParam(defaultValue = "false") boolean hasRecipe) {
    return ingredientService.getIngredients(hasRecipe);
  }

  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  public IngredientResponse createIngredient(@Valid @RequestBody IngredientRequest request) {
    return ingredientService.createIngredient(request.name());
  }

  @GetMapping("/{publicId}")
  public IngredientResponse getIngredient(@PathVariable String publicId) {
    return ingredientService.getIngredient(publicId);
  }
}
