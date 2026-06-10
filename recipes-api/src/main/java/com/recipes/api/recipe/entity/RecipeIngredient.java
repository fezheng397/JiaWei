package com.recipes.api.recipe.entity;

import com.recipes.api.common.AuditedEntity;
import com.recipes.api.ingredient.entity.Ingredient;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe_ingredients")
public class RecipeIngredient extends AuditedEntity {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "ingredient_id", nullable = false)
  private Ingredient ingredient;

  @Column(nullable = false)
  private String quantity;

  private String unit;

  @Column(nullable = false)
  private int position;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "prepared_by_recipe_id")
  private Recipe preparedByRecipe;

  protected RecipeIngredient() {}

  public RecipeIngredient(
      Recipe recipe,
      Ingredient ingredient,
      String quantity,
      String unit,
      int position,
      Recipe preparedByRecipe) {
    this.recipe = recipe;
    this.ingredient = ingredient;
    this.quantity = quantity;
    this.unit = unit;
    this.position = position;
    this.preparedByRecipe = preparedByRecipe;
  }

  public Recipe getRecipe() {
    return recipe;
  }

  public Ingredient getIngredient() {
    return ingredient;
  }

  public String getQuantity() {
    return quantity;
  }

  public String getUnit() {
    return unit;
  }

  public int getPosition() {
    return position;
  }

  public Recipe getPreparedByRecipe() {
    return preparedByRecipe;
  }
}
