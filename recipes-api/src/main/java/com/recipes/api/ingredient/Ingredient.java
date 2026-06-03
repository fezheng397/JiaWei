package com.recipes.api.ingredient;

import com.recipes.api.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "ingredients")
public class Ingredient extends AuditedEntity {
  @Column(nullable = false)
  private String name;

  protected Ingredient() {}

  public Ingredient(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
