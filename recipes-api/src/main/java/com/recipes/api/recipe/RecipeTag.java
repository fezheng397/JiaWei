package com.recipes.api.recipe;

import com.recipes.api.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "recipe_tags")
public class RecipeTag extends AuditedEntity {
  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "recipe_id", nullable = false)
  private Recipe recipe;

  @Column(nullable = false)
  private int position;

  @Column(nullable = false)
  private String tag;

  protected RecipeTag() {}

  public Recipe getRecipe() {
    return recipe;
  }

  public int getPosition() {
    return position;
  }

  public String getTag() {
    return tag;
  }
}
