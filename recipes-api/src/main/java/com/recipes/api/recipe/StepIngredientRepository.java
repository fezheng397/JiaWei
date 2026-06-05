package com.recipes.api.recipe;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface StepIngredientRepository
    extends JpaRepository<StepIngredient, StepIngredientId> {
  @EntityGraph(
      attributePaths = {
        "step",
        "recipeIngredient",
        "recipeIngredient.ingredient",
        "recipeIngredient.preparedByRecipe"
      })
  @Query("select link from StepIngredient link where link.step.recipe.id = :recipeId")
  List<StepIngredient> findByRecipeId(@Param("recipeId") UUID recipeId);
}
