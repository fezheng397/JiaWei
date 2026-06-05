package com.recipes.api.author;

import com.recipes.api.common.NotFoundException;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AuthorService {
  private final AuthorRepository authorRepository;

  public AuthorService(AuthorRepository authorRepository) {
    this.authorRepository = authorRepository;
  }

  public List<AuthorResponse> getAuthors() {
    return authorRepository.findAllByOrderByName().stream().map(this::toResponse).toList();
  }

  public Author getAuthor(String publicId) {
    return authorRepository
        .findByPublicId(publicId)
        .orElseThrow(() -> new NotFoundException("Author not found: " + publicId));
  }

  @Transactional
  public Author findOrCreate(String publicId, String name) {
    if (publicId != null && !publicId.isBlank()) {
      return getAuthor(publicId);
    }

    String normalizedName = normalizeName(name);
    return authorRepository
        .findByNameIgnoringCase(normalizedName)
        .orElseGet(() -> authorRepository.save(new Author(normalizedName)));
  }

  private AuthorResponse toResponse(Author author) {
    return new AuthorResponse(author.getPublicId(), author.getName());
  }

  private String normalizeName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("Author name is required");
    }

    return name.trim().replaceAll("\\s+", " ");
  }
}
