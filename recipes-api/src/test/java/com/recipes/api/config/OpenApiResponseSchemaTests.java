package com.recipes.api.config;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import com.recipes.api.common.ApiError;
import com.recipes.api.ingredient.IngredientRequest;
import com.recipes.api.ingredient.IngredientResponse;
import com.recipes.api.recipe.IngredientLineResponse;
import com.recipes.api.recipe.RecipeIngredientRedirectResponse;
import com.recipes.api.recipe.RecipeResponse;
import com.recipes.api.recipe.RecipeSummaryResponse;
import com.recipes.api.recipe.StepResponse;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.media.Schema;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;

class OpenApiResponseSchemaTests {
  @Test
  void responseSchemasRequireEverySerializedProperty() {
    assertAllPropertiesRequired(ApiError.class);
    assertAllPropertiesRequired(IngredientResponse.class);
    assertAllPropertiesRequired(IngredientLineResponse.class);
    assertAllPropertiesRequired(RecipeIngredientRedirectResponse.class);
    assertAllPropertiesRequired(RecipeResponse.class);
    assertAllPropertiesRequired(RecipeSummaryResponse.class);
    assertAllPropertiesRequired(StepResponse.class);
  }

  @Test
  void responseSchemasOnlyMarkActuallyNullableValuesNullable() {
    assertNullableProperties(
        IngredientLineResponse.class, "preparedByRecipePublicId", "preparedByRecipeName");
    assertNullableProperties(
        RecipeResponse.class,
        "publishedAt",
        "ingredientPublicId",
        "heroImageUrl",
        "prepTimeMinutes",
        "cookTimeMinutes",
        "totalTimeMinutes",
        "difficulty",
        "servings",
        "yieldAmount");
    assertNullableProperties(
        RecipeSummaryResponse.class,
        "publishedAt",
        "ingredientPublicId",
        "heroImageUrl",
        "totalTimeMinutes",
        "difficulty",
        "servings",
        "yieldAmount");
    assertNullableProperties(StepResponse.class, "timerMinutes");
    assertNullableProperties(ApiError.class);
    assertNullableProperties(RecipeIngredientRedirectResponse.class);
  }

  @Test
  void requestSchemaOnlyRequiresActuallyRequiredInput() {
    Schema<?> schema = schemaFor(IngredientRequest.class);

    assertEquals(Set.of("name"), new HashSet<>(schema.getRequired()));
  }

  @Test
  void recipeSchemasDocumentEnumValues() {
    Schema<?> schema = schemaFor(RecipeSummaryResponse.class);

    assertEquals(List.of("dish", "ingredient"), property(schema, "kind").getEnum());
    assertEquals(List.of("easy", "medium", "hard"), property(schema, "difficulty").getEnum());
  }

  @Test
  void nullableResponseReferenceUsesOneOf() {
    io.swagger.v3.oas.annotations.media.Schema annotation =
        IngredientResponse.class.getRecordComponents()[2]
            .getAccessor()
            .getAnnotation(io.swagger.v3.oas.annotations.media.Schema.class);
    Schema<?> nullableReference =
        new Schema<>()
            .$ref("#/components/schemas/RecipeSummaryResponse")
            .types(Set.of("null"));
    Schema<?> ingredientResponse = new Schema<>().addProperty("madeByRecipe", nullableReference);
    OpenAPI openApi =
        new OpenAPI()
            .components(new Components().addSchemas("IngredientResponse", ingredientResponse));

    OpenApiConfig.normalizeNullableReferences(openApi);

    Schema<?> madeByRecipe = property(ingredientResponse, "madeByRecipe");
    assertEquals(true, annotation.nullable());
    assertEquals(2, madeByRecipe.getOneOf().size());
    assertEquals(
        "#/components/schemas/RecipeSummaryResponse", madeByRecipe.getOneOf().getFirst().get$ref());
    assertEquals(Set.of("null"), madeByRecipe.getOneOf().getLast().getTypes());
  }

  private static void assertAllPropertiesRequired(Class<?> responseType) {
    Schema<?> schema = schemaFor(responseType);

    assertEquals(schema.getProperties().keySet(), new HashSet<>(schema.getRequired()));
  }

  private static void assertNullableProperties(Class<?> responseType, String... nullableProperties) {
    Schema<?> schema = schemaFor(responseType);
    Set<String> expectedNullable = Set.of(nullableProperties);
    Set<String> actualNullable =
        schema.getProperties().entrySet().stream()
            .filter(entry -> isNullable((Schema<?>) entry.getValue()))
            .map(Map.Entry::getKey)
            .collect(Collectors.toSet());

    assertEquals(expectedNullable, actualNullable);
  }

  private static boolean isNullable(Schema<?> schema) {
    return Boolean.TRUE.equals(schema.getNullable())
        || "null".equals(schema.getType())
        || schema.getTypes() != null && schema.getTypes().contains("null");
  }

  private static Schema<?> schemaFor(Class<?> responseType) {
    Schema<?> schema =
        ModelConverters.getInstance().readAll(responseType).get(responseType.getSimpleName());
    assertNotNull(schema);
    assertNotNull(schema.getProperties());
    assertNotNull(schema.getRequired());
    return schema;
  }

  private static Schema<?> property(Schema<?> schema, String name) {
    Schema<?> property = (Schema<?>) schema.getProperties().get(name);
    assertNotNull(property);
    return property;
  }
}
