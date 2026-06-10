package com.recipes.api.author;

import com.recipes.api.author.dto.AuthorResponse;
import com.recipes.api.author.entity.Author;
import com.recipes.api.author.repository.AuthorRepository;
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

  private AuthorResponse toResponse(Author author) {
    return new AuthorResponse(author.getPublicId(), author.getName());
  }
}
