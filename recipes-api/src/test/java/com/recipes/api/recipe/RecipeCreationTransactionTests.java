package com.recipes.api.recipe;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.recipes.api.author.entity.Author;
import com.recipes.api.author.repository.AuthorRepository;
import com.recipes.api.ingredient.entity.Ingredient;
import com.recipes.api.ingredient.repository.IngredientRepository;
import com.recipes.api.recipe.dto.RecipeCreateRequest;
import com.recipes.api.recipe.dto.RecipeIngredientCreateRequest;
import com.recipes.api.recipe.dto.RecipeStepCreateRequest;
import com.recipes.api.recipe.repository.RecipeIngredientRepository;
import com.recipes.api.recipe.repository.RecipeRepository;
import com.recipes.api.recipe.repository.RecipeStepRepository;
import com.recipes.api.recipe.repository.StepIngredientRepository;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

@SpringBootTest
class RecipeCreationTransactionTests {
  @Autowired private RecipeService recipeService;
  @Autowired private AuthorRepository authorRepository;
  @Autowired private IngredientRepository ingredientRepository;
  @Autowired private RecipeRepository recipeRepository;
  @Autowired private RecipeIngredientRepository recipeIngredientRepository;
  @Autowired private RecipeStepRepository recipeStepRepository;
  @Autowired private StepIngredientRepository stepIngredientRepository;
  @Autowired private PlatformTransactionManager transactionManager;

  @Test
  void rollsBackEntireRecipeGraphWhenTransactionFails() {
    TransactionTemplate transaction = new TransactionTemplate(transactionManager);
    String suffix = UUID.randomUUID().toString();
    References references =
        transaction.execute(
            status -> {
              Author author =
                  authorRepository.save(new Author("Transaction Test Author " + suffix));
              Ingredient ingredient =
                  ingredientRepository.save(new Ingredient("transaction test ingredient " + suffix));
              return new References(
                  author.getId(), author.getPublicId(), ingredient.getId(), ingredient.getPublicId());
            });
    GraphCounts countsBefore = graphCounts();
    RecipeCreateRequest request =
        new RecipeCreateRequest(
            "dish",
            "Transaction Test Recipe " + suffix,
            "This recipe must be rolled back.",
            references.authorPublicId(),
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
                    "ingredient-line", references.ingredientPublicId(), "1", null, null)),
            List.of(
                new RecipeStepCreateRequest(
                    "Trigger rollback after creation.", 10, List.of("ingredient-line"))));

    try {
      assertThrows(
          DeliberateRollbackException.class,
          () ->
              transaction.executeWithoutResult(
                  status -> {
                    recipeService.createRecipe(request);
                    throw new DeliberateRollbackException();
                  }));

      assertEquals(countsBefore, graphCounts());
    } finally {
      transaction.executeWithoutResult(
          status -> {
            ingredientRepository.deleteById(references.ingredientId());
            authorRepository.deleteById(references.authorId());
          });
    }
  }

  private GraphCounts graphCounts() {
    return new GraphCounts(
        recipeRepository.count(),
        recipeIngredientRepository.count(),
        recipeStepRepository.count(),
        stepIngredientRepository.count());
  }

  private record References(
      UUID authorId, String authorPublicId, UUID ingredientId, String ingredientPublicId) {}

  private record GraphCounts(long recipes, long ingredientLines, long steps, long stepIngredients) {}

  private static class DeliberateRollbackException extends RuntimeException {}
}
