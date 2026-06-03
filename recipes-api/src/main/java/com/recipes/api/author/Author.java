package com.recipes.api.author;

import com.recipes.api.common.AuditedEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;

@Entity
@Table(name = "authors")
public class Author extends AuditedEntity {
  @Column(nullable = false)
  private String name;

  protected Author() {}

  public Author(String name) {
    this.name = name;
  }

  public String getName() {
    return name;
  }
}
