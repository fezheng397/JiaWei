package com.recipes.api.recipe;

import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

@Entity
@Table(name = "step_ingredients")
public class StepIngredient {
  @EmbeddedId private StepIngredientId id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("stepId")
  @JoinColumn(name = "step_id", nullable = false)
  private RecipeStep step;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @MapsId("recipeIngredientId")
  @JoinColumn(name = "recipe_ingredient_id", nullable = false)
  private RecipeIngredient recipeIngredient;

  protected StepIngredient() {}

  static StepIngredient create(RecipeStep step, RecipeIngredient recipeIngredient) {
    StepIngredient stepIngredient = new StepIngredient();
    stepIngredient.id = new StepIngredientId(step.getId(), recipeIngredient.getId());
    stepIngredient.step = step;
    stepIngredient.recipeIngredient = recipeIngredient;
    return stepIngredient;
  }

  public StepIngredientId getId() {
    return id;
  }

  public RecipeStep getStep() {
    return step;
  }

  public RecipeIngredient getRecipeIngredient() {
    return recipeIngredient;
  }
}
