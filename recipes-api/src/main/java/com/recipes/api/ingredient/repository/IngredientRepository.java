package com.recipes.api.ingredient.repository;

import com.recipes.api.ingredient.entity.Ingredient;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
  List<Ingredient> findAllByOrderByName();

  @Query(
      """
      select ingredient
      from Ingredient ingredient
      where exists (
        select recipe
        from Recipe recipe
        where recipe.ingredient = ingredient
      )
      order by ingredient.name
      """)
  List<Ingredient> findAllHavingRecipeOrderByName();

  Optional<Ingredient> findByPublicId(String publicId);

  List<Ingredient> findAllByPublicIdIn(Collection<String> publicIds);

  @Query("select ingredient from Ingredient ingredient where lower(ingredient.name) = lower(:name)")
  Optional<Ingredient> findByNameIgnoringCase(@Param("name") String name);
}
