package com.recipes.api.author;

import java.util.UUID;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthorRepository extends JpaRepository<Author, UUID> {
  List<Author> findAllByOrderByName();

  Optional<Author> findByPublicId(String publicId);

  Optional<Author> findByNameIgnoringCase(String name);
}
