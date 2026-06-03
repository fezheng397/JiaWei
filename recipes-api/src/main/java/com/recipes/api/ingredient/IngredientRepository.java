package com.recipes.api.ingredient;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IngredientRepository extends JpaRepository<Ingredient, UUID> {
  List<Ingredient> findAllByOrderByName();

  Optional<Ingredient> findByPublicId(String publicId);
}
