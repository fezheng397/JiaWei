package com.recipes.api.recipe;

import com.recipes.api.common.AuditedEntity;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "step_ingredients")
public class StepIngredient extends AuditedEntity {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "step_id", nullable = false)
  private RecipeStep step;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "recipe_ingredient_id", nullable = false)
  private RecipeIngredient recipeIngredient;

  protected StepIngredient() {}

  public RecipeStep getStep() {
    return step;
  }

  public RecipeIngredient getRecipeIngredient() {
    return recipeIngredient;
  }
}
