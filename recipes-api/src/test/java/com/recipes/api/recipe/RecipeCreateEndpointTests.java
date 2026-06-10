package com.recipes.api.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.recipes.api.author.entity.Author;
import com.recipes.api.author.repository.AuthorRepository;
import com.recipes.api.ingredient.entity.Ingredient;
import com.recipes.api.ingredient.repository.IngredientRepository;
import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.dto.RecipeIngredientCreateRequest;
import com.recipes.api.recipe.dto.RecipeResponse;
import com.recipes.api.recipe.dto.RecipeStepCreateRequest;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class RecipeCreateEndpointTests {
  @Autowired private MockMvc mockMvc;
  @Autowired private ObjectMapper objectMapper;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private IngredientRepository ingredientRepository;

  @Test
  void returnsCreatedRecipeAndLocationThatResolvesToSameResource() throws Exception {
    Author author = authorRepository.saveAndFlush(new Author("Recipe Endpoint Test Author"));
    Ingredient ingredient =
        ingredientRepository.saveAndFlush(new Ingredient("recipe endpoint test ingredient"));
    RecipeCreateRequest request =
        new RecipeCreateRequest(
            "dish",
            "Endpoint Test Recipe",
            "Created through POST /recipes.",
            author.getPublicId(),
            List.of("test"),
            null,
            5,
            10,
            "easy",
            2,
            null,
            null,
            null,
            List.of(
                new RecipeIngredientCreateRequest(
                    "ingredient-line", ingredient.getPublicId(), "1", "cup", null)),
            List.of(
                new RecipeStepCreateRequest(
                    "Cook the ingredient.", 10, List.of("ingredient-line"))));

    MvcResult createResult =
        mockMvc
            .perform(
                post("/recipes")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsBytes(request)))
            .andExpect(status().isCreated())
            .andExpect(header().exists("Location"))
            .andReturn();
    RecipeResponse created =
        objectMapper.readValue(createResult.getResponse().getContentAsByteArray(), RecipeResponse.class);
    String location = createResult.getResponse().getHeader("Location");

    assertNotNull(location);
    assertEquals("/recipes/" + created.publicId(), location);
    assertEquals("Endpoint Test Recipe", created.name());
    assertEquals(1, created.ingredients().size());
    assertEquals(1, created.steps().size());

    MvcResult getResult =
        mockMvc.perform(get(location)).andExpect(status().isOk()).andReturn();
    RecipeResponse fetched =
        objectMapper.readValue(getResult.getResponse().getContentAsByteArray(), RecipeResponse.class);

    assertEquals(created, fetched);
  }
}
