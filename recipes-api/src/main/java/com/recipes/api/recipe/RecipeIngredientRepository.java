package com.recipes.api.recipe;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeIngredientRepository
    extends JpaRepository<RecipeIngredient, UUID> {
  @EntityGraph(attributePaths = {"ingredient", "preparedByRecipe"})
  @Query(
      """
      select ingredient
      from RecipeIngredient ingredient
      where ingredient.recipe.id = :recipeId
      order by ingredient.position
      """)
  List<RecipeIngredient> findByRecipeIdOrderByPosition(@Param("recipeId") UUID recipeId);

  void deleteByRecipe(Recipe recipe);
}
