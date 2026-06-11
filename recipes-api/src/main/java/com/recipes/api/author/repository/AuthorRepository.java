package com.recipes.api.author.repository;

import com.recipes.api.author.entity.Author;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
  List<Author> findAllByOrderByName();

  Optional<Author> findByPublicId(String publicId);
}
