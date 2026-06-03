package com.recipes.api.ingredient;

import com.recipes.api.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredients")
public class Ingredient extends AuditedEntity {
  @Column(nullable = false)
  private String name;

  protected Ingredient() {}

  public Ingredient(String name) {
    this.name = IngredientNameNormalizer.normalize(name);
  }

  public String getName() {
    return name;
  }

  @PrePersist
  @PreUpdate
  void normalizeName() {
    name = IngredientNameNormalizer.normalize(name);
  }
}
