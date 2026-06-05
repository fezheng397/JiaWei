package com.recipes.api.recipe;

import com.recipes.api.common.AuditedEntity;
import com.recipes.api.ingredient.Ingredient;
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

  static RecipeIngredient create(
      Recipe recipe,
      Ingredient ingredient,
      String quantity,
      String unit,
      int position,
      Recipe preparedByRecipe) {
    RecipeIngredient recipeIngredient = new RecipeIngredient();
    recipeIngredient.recipe = recipe;
    recipeIngredient.ingredient = ingredient;
    recipeIngredient.quantity = quantity;
    recipeIngredient.unit = unit;
    recipeIngredient.position = position;
    recipeIngredient.preparedByRecipe = preparedByRecipe;
    return recipeIngredient;
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
