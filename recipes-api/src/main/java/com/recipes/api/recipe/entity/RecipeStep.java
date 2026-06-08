package com.recipes.api.recipe.entity;

import com.recipes.api.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe_steps")
public class RecipeStep extends AuditedEntity {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  @Column(nullable = false)
  private int position;

  @Column(nullable = false, columnDefinition = "text")
  private String instructions;

  private Integer timerMinutes;

  protected RecipeStep() {}

  public Recipe getRecipe() {
    return recipe;
  }

  public int getPosition() {
    return position;
  }

  public String getInstructions() {
    return instructions;
  }

  public Integer getTimerMinutes() {
    return timerMinutes;
  }
}
