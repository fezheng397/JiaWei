package com.recipes.api.recipe.entity;

import com.recipes.api.author.entity.Author;
import com.recipes.api.common.AuditedEntity;
import com.recipes.api.ingredient.entity.Ingredient;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OrderColumn;
import jakarta.persistence.Table;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "recipes")
public class Recipe extends AuditedEntity {
  @Column(nullable = false)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "author_id", nullable = false)
  private Author author;

  @Column(nullable = false, columnDefinition = "text")
  private String description;

  private OffsetDateTime publishedAt;
  private String heroImageUrl;
  private Integer prepTimeMinutes;
  private Integer cookTimeMinutes;

  @Enumerated(EnumType.STRING)
  private RecipeDifficulty difficulty;

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private RecipeKind kind;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "ingredient_id")
  private Ingredient ingredient;

  private Integer servings;
  private String yieldQuantity;
  private String yieldUnit;

  @ElementCollection
  @CollectionTable(name = "recipe_tags", joinColumns = @JoinColumn(name = "recipe_id"))
  @OrderColumn(name = "position")
  @Column(name = "tag", nullable = false)
  private List<String> tags = new ArrayList<>();

  protected Recipe() {}

  public Recipe(
      String name,
      Author author,
      String description,
      String heroImageUrl,
      Integer prepTimeMinutes,
      Integer cookTimeMinutes,
      RecipeDifficulty difficulty,
      RecipeKind kind,
      Ingredient ingredient,
      Integer servings,
      String yieldQuantity,
      String yieldUnit,
      List<String> tags) {
    this.name = name;
    this.author = author;
    this.description = description;
    this.heroImageUrl = heroImageUrl;
    this.prepTimeMinutes = prepTimeMinutes;
    this.cookTimeMinutes = cookTimeMinutes;
    this.difficulty = difficulty;
    this.kind = kind;
    this.ingredient = ingredient;
    this.servings = servings;
    this.yieldQuantity = yieldQuantity;
    this.yieldUnit = yieldUnit;
    this.tags = new ArrayList<>(tags);
  }

  public String getName() {
    return name;
  }

  public Author getAuthor() {
    return author;
  }

  public String getDescription() {
    return description;
  }

  public OffsetDateTime getPublishedAt() {
    return publishedAt;
  }

  public String getHeroImageUrl() {
    return heroImageUrl;
  }

  public Integer getPrepTimeMinutes() {
    return prepTimeMinutes;
  }

  public Integer getCookTimeMinutes() {
    return cookTimeMinutes;
  }

  public RecipeDifficulty getDifficulty() {
    return difficulty;
  }

  public RecipeKind getKind() {
    return kind;
  }

  public Ingredient getIngredient() {
    return ingredient;
  }

  public Integer getServings() {
    return servings;
  }

  public String getYieldQuantity() {
    return yieldQuantity;
  }

  public String getYieldUnit() {
    return yieldUnit;
  }

  public List<String> getTags() {
    return List.copyOf(tags);
  }
}
