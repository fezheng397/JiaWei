package com.recipes.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.json.JsonMapper;
import com.recipes.api.ingredient.IngredientResponse;
import com.recipes.api.recipe.IngredientLineResponse;
import com.recipes.api.recipe.RecipeResponse;
import com.recipes.api.recipe.RecipeSummaryResponse;
import com.recipes.api.recipe.StepResponse;
import java.util.List;
import java.util.Set;
import org.junit.jupiter.api.Test;

class ResponseSerializationContractTests {
  private final JsonMapper jsonMapper = JsonMapper.builder().findAndAddModules().build();

  @Test
  void recipeSummaryIncludesEveryKeyAndSerializesUnavailableValuesAsNull() throws Exception {
    RecipeSummaryResponse response =
        new RecipeSummaryResponse(
            "recipe-id",
            "Recipe",
            "Description",
            "Category",
            "Author",
            null,
            "dish",
            null,
            null,
            null,
            null,
            List.of(),
            null,
            null);

    JsonNode json = jsonMapper.valueToTree(response);

    assertKeys(
        json,
        "publicId",
        "name",
        "description",
        "categoryLabel",
        "authorName",
        "publishedAt",
        "kind",
        "ingredientPublicId",
        "heroImageUrl",
        "totalTimeMinutes",
        "difficulty",
        "tags",
        "servings",
        "yieldAmount");
    assertNullValues(
        json,
        "publishedAt",
        "ingredientPublicId",
        "heroImageUrl",
        "totalTimeMinutes",
        "difficulty",
        "servings",
        "yieldAmount");
  }

  @Test
  void nestedNullableReferencesAndCollectionsKeepTheirKeys() throws Exception {
    IngredientResponse ingredient = new IngredientResponse("ingredient-id", "Salt", null, List.of());
    IngredientLineResponse ingredientLine =
        new IngredientLineResponse("line-id", "ingredient-id", "1 tsp", "Salt", null, null);
    StepResponse step = new StepResponse("step-id", 1, "Season.", null, "", List.of());
    RecipeResponse recipe =
        new RecipeResponse(
            "recipe-id",
            "Recipe",
            "Description",
            "Category",
            "Author",
            null,
            "dish",
            null,
            null,
            null,
            null,
            null,
            null,
            List.of(),
            null,
            null,
            List.of(ingredientLine),
            List.of(step));

    JsonNode ingredientJson = jsonMapper.valueToTree(ingredient);
    JsonNode recipeJson = jsonMapper.valueToTree(recipe);

    assertKeys(ingredientJson, "publicId", "name", "madeByRecipe", "usedInRecipes");
    assertTrue(ingredientJson.get("madeByRecipe").isNull());
    assertTrue(ingredientJson.get("usedInRecipes").isArray());
    assertTrue(recipeJson.get("ingredients").isArray());
    assertTrue(recipeJson.get("steps").isArray());
    assertTrue(recipeJson.get("ingredients").get(0).get("preparedByRecipePublicId").isNull());
    assertTrue(recipeJson.get("steps").get(0).get("timerMinutes").isNull());
  }

  private static void assertKeys(JsonNode json, String... expectedKeys) {
    Set<String> actualKeys =
        json.properties().stream()
            .map(java.util.Map.Entry::getKey)
            .collect(java.util.stream.Collectors.toSet());
    assertEquals(Set.of(expectedKeys), actualKeys);
  }

  private static void assertNullValues(JsonNode json, String... keys) {
    for (String key : keys) {
      assertTrue(json.get(key).isNull(), key + " should serialize as null");
    }
  }
}
