package com.recipes.api.author;

import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;

import com.recipes.api.author.entity.Author;
import com.recipes.api.author.repository.AuthorRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class AuthorControllerTests {
  @Autowired private MockMvc mockMvc;
  @Autowired private AuthorRepository authorRepository;

  @Test
  void listsAuthorsUsingPublicContractFields() throws Exception {
    Author author = authorRepository.saveAndFlush(new Author("Author Endpoint Test"));

    mockMvc
        .perform(get("/authors"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
        .andExpect(jsonPath("$").isArray())
        .andExpect(jsonPath("$", hasSize((int) authorRepository.count())))
        .andExpect(
            jsonPath(
                "$",
                hasItem(
                    org.hamcrest.Matchers.allOf(
                        org.hamcrest.Matchers.hasEntry("publicId", author.getPublicId()),
                        org.hamcrest.Matchers.hasEntry("name", author.getName())))))
        .andExpect(jsonPath("$[?(@.id)]").isEmpty())
        .andExpect(jsonPath("$[?(@.createdAt)]").isEmpty());
  }
}
