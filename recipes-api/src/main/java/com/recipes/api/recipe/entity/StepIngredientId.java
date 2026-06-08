package com.recipes.api.recipe.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;
import java.util.UUID;

@Embeddable
public class StepIngredientId implements Serializable {
  @Column(name = "step_id", nullable = false, columnDefinition = "uuid")
  private UUID stepId;

  @Column(name = "recipe_ingredient_id", nullable = false, columnDefinition = "uuid")
  private UUID recipeIngredientId;

  protected StepIngredientId() {}

  StepIngredientId(UUID stepId, UUID recipeIngredientId) {
    this.stepId = stepId;
    this.recipeIngredientId = recipeIngredientId;
  }

  public UUID getStepId() {
    return stepId;
  }

  public UUID getRecipeIngredientId() {
    return recipeIngredientId;
  }

  @Override
  public boolean equals(Object other) {
    if (this == other) {
      return true;
    }
    if (!(other instanceof StepIngredientId that)) {
      return false;
    }
    return Objects.equals(stepId, that.stepId)
        && Objects.equals(recipeIngredientId, that.recipeIngredientId);
  }

  @Override
  public int hashCode() {
    return Objects.hash(stepId, recipeIngredientId);
  }
}
