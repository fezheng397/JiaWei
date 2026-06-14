package com.recipes.api.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.recipes.api.author.entity.Author;
import com.recipes.api.author.repository.AuthorRepository;
import com.recipes.api.ingredient.entity.Ingredient;
import com.recipes.api.ingredient.repository.IngredientRepository;
import com.recipes.api.media.entity.MediaAsset;
import com.recipes.api.media.entity.MediaAssetStatus;
import com.recipes.api.media.entity.MediaType;
import com.recipes.api.media.repository.MediaAssetRepository;
import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.dto.RecipeIngredientCreateRequest;
import com.recipes.api.recipe.dto.RecipeResponse;
import com.recipes.api.recipe.dto.RecipeStepCreateRequest;
import com.recipes.api.recipe.entity.Recipe;
import com.recipes.api.recipe.repository.RecipeIngredientRepository;
import com.recipes.api.recipe.repository.RecipeRepository;
import com.recipes.api.recipe.repository.RecipeStepRepository;
import com.recipes.api.recipe.repository.StepIngredientRepository;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Transactional
class RecipeCreationPersistenceTests {
  @Autowired private RecipeService recipeService;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private IngredientRepository ingredientRepository;
  @Autowired private MediaAssetRepository mediaAssetRepository;
  @Autowired private RecipeRepository recipeRepository;
  @Autowired private RecipeIngredientRepository recipeIngredientRepository;
  @Autowired private RecipeStepRepository recipeStepRepository;
  @Autowired private StepIngredientRepository stepIngredientRepository;

  @Test
  void createsRecipeIngredientLinesStepsAndStepIngredientLinks() {
    Author author = authorRepository.saveAndFlush(new Author("Recipe Creation Test Author"));
    Ingredient rice = ingredientRepository.saveAndFlush(new Ingredient("recipe creation rice"));
    Ingredient water = ingredientRepository.saveAndFlush(new Ingredient("recipe creation water"));
    RecipeCreateRequest request =
        new RecipeCreateRequest(
            "dish",
            "Test Rice",
            "A recipe created by the persistence test.",
            author.getPublicId(),
            List.of("test", "rice"),
            null,
            5,
            20,
            "easy",
            2,
            null,
            null,
            null,
            List.of(
                new RecipeIngredientCreateRequest(
                    "rice-line", rice.getPublicId(), "1", "cup", null),
                new RecipeIngredientCreateRequest(
                    "water-line", water.getPublicId(), "2", "cups", null)),
            List.of(
                new RecipeStepCreateRequest(
                    "Rinse the rice.", null, List.of("rice-line")),
                new RecipeStepCreateRequest(
                    "Cook the rice in water.", 20, List.of("rice-line", "water-line"))));

    RecipeResponse response = recipeService.createRecipe(request);

    assertNotNull(response.publicId());
    assertEquals("Test Rice", response.name());
    assertEquals(List.of("test", "rice"), response.tags());
    assertEquals(2, response.ingredients().size());
    assertEquals(2, response.steps().size());
    assertEquals(1, response.steps().getFirst().ingredientDetails().size());
    assertEquals(2, response.steps().getLast().ingredientDetails().size());

    Recipe recipe = recipeRepository.findByPublicId(response.publicId()).orElseThrow();
    assertEquals(
        List.of(1, 2),
        recipeIngredientRepository.findByRecipeIdOrderByPosition(recipe.getId()).stream()
            .map(ingredient -> ingredient.getPosition())
            .toList());
    assertEquals(
        List.of(1, 2),
        recipeStepRepository.findByRecipeIdOrderByPosition(recipe.getId()).stream()
            .map(step -> step.getPosition())
            .toList());
    assertEquals(3, stepIngredientRepository.findByRecipeId(recipe.getId()).size());
  }

  @Test
  void createsIngredientRecipeAndPreparedByRecipeRelationship() {
    Author author =
        authorRepository.saveAndFlush(new Author("Prepared Recipe Creation Test Author"));
    Ingredient pork =
        ingredientRepository.saveAndFlush(new Ingredient("prepared recipe creation pork"));
    Ingredient groundPork =
        ingredientRepository.saveAndFlush(new Ingredient("prepared recipe creation ground pork"));
    RecipeResponse preparedRecipe =
        recipeService.createRecipe(
            new RecipeCreateRequest(
                "ingredient",
                "Test Ground Pork",
                "Prepare ground pork.",
                author.getPublicId(),
                List.of("prep"),
                null,
                10,
                null,
                "easy",
                null,
                groundPork.getPublicId(),
                "1",
                "lb",
                List.of(
                    new RecipeIngredientCreateRequest(
                        "pork-line", pork.getPublicId(), "1", "lb", null)),
                List.of(
                    new RecipeStepCreateRequest(
                        "Grind the pork.", null, List.of("pork-line")))));
    RecipeResponse dishRecipe =
        recipeService.createRecipe(
            new RecipeCreateRequest(
                "dish",
                "Test Pork Dish",
                "Cook prepared pork.",
                author.getPublicId(),
                List.of("test"),
                null,
                null,
                10,
                "easy",
                2,
                null,
                null,
                null,
                List.of(
                    new RecipeIngredientCreateRequest(
                        "ground-pork-line",
                        groundPork.getPublicId(),
                        "1",
                        "lb",
                        preparedRecipe.publicId())),
                List.of(
                    new RecipeStepCreateRequest(
                        "Cook the ground pork.", 10, List.of("ground-pork-line")))));

    assertEquals(groundPork.getPublicId(), preparedRecipe.ingredientPublicId());
    assertEquals("1 lb", preparedRecipe.yieldAmount());
    assertEquals(
        preparedRecipe.publicId(),
        dishRecipe.ingredients().getFirst().preparedByRecipePublicId());
    assertEquals(
        preparedRecipe.publicId(),
        dishRecipe.steps().getFirst().ingredientDetails().getFirst().preparedByRecipePublicId());
  }

  @Test
  void associatesUploadedHeroImageAndUsesItsPublicUrl() {
    Author author = authorRepository.saveAndFlush(new Author("Hero Image Test Author"));
    Ingredient salt = ingredientRepository.saveAndFlush(new Ingredient("hero image test salt"));
    MediaAsset heroImage =
        new MediaAsset(
            MediaType.IMAGE,
            "recipes/hero-image-test.jpg",
            "https://media.jiawei.app/recipes/hero-image-test.jpg",
            "hero-image-test.jpg",
            "image/jpeg",
            2048);
    heroImage.markUploaded();
    mediaAssetRepository.saveAndFlush(heroImage);

    RecipeResponse response =
        recipeService.createRecipe(
            new RecipeCreateRequest(
                "dish",
                "Hero Image Test Recipe",
                "A recipe with a persisted hero image.",
                author.getPublicId(),
                List.of("test"),
                "https://legacy.jiawei.app/hero-image-test.jpg",
                heroImage.getPublicId(),
                null,
                null,
                "easy",
                2,
                null,
                null,
                null,
                List.of(
                    new RecipeIngredientCreateRequest(
                        "salt-line", salt.getPublicId(), "1", "tsp", null)),
                List.of(new RecipeStepCreateRequest("Add salt.", null, List.of("salt-line")))));

    Recipe recipe = recipeRepository.findByPublicId(response.publicId()).orElseThrow();

    assertEquals(heroImage.getId(), recipe.getHeroImage().getId());
    assertEquals(heroImage.getPublicId(), response.heroImagePublicId());
    assertEquals(heroImage.getPublicUrl(), response.heroImageUrl());
    assertEquals(MediaAssetStatus.UPLOADED, heroImage.getStatus());
  }
}
