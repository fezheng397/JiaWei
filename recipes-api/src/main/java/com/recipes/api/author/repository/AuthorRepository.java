package com.recipes.api.author.repository;

import com.recipes.api.author.entity.Author;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
  Optional<Author> findByPublicId(String publicId);
}
