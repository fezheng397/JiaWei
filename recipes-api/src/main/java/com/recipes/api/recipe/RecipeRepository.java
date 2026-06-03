package com.recipes.api.recipe;

import com.recipes.api.ingredient.Ingredient;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RecipeRepository extends JpaRepository<Recipe, UUID> {
  @EntityGraph(attributePaths = {"author", "ingredient", "tags"})
  List<Recipe> findByKindOrderByName(RecipeKind kind);

  @EntityGraph(attributePaths = {"author", "ingredient", "tags"})
  @Query(
      """
      select recipe
      from Recipe recipe
      where recipe.kind = :kind and recipe.ingredient = :ingredient
      """)
  Optional<Recipe> findByKindAndIngredient(
      @Param("kind") RecipeKind kind, @Param("ingredient") Ingredient ingredient);

  @EntityGraph(attributePaths = {"author", "ingredient", "tags"})
  Optional<Recipe> findByPublicId(String publicId);

  @EntityGraph(attributePaths = {"author", "ingredient", "tags"})
  List<Recipe> findAllByOrderByName();

  @EntityGraph(attributePaths = {"author", "ingredient", "tags"})
  @Query(
      """
      select distinct recipe
      from RecipeIngredient recipeIngredient
      join recipeIngredient.recipe recipe
      where recipeIngredient.ingredient = :ingredient
      order by recipe.name
      """)
  List<Recipe> findUsedByIngredient(@Param("ingredient") Ingredient ingredient);
}
