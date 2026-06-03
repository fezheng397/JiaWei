package com.recipes.api.recipe;

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
      where recipe.kind = :kind and recipe.ingredient.id = :ingredientId
      """)
  Optional<Recipe> findByKindAndIngredientId(
      @Param("kind") RecipeKind kind, @Param("ingredientId") UUID ingredientId);

  @EntityGraph(attributePaths = {"author", "ingredient", "tags"})
  Optional<Recipe> findByPublicId(String publicId);

  @EntityGraph(attributePaths = {"author", "ingredient", "tags"})
  List<Recipe> findAllByOrderByName();
}
