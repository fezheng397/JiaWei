package com.recipes.api.recipe;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeStepRepository extends JpaRepository<RecipeStep, UUID> {
  @Query(
      """
      select step
      from RecipeStep step
      where step.recipe.id = :recipeId
      order by step.position
      """)
  List<RecipeStep> findByRecipeIdOrderByPosition(@Param("recipeId") UUID recipeId);

  void deleteByRecipe(Recipe recipe);
}
