package com.recipes.api.config;

import com.recipes.api.common.ApiError;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.media.ComposedSchema;
import io.swagger.v3.oas.models.media.Schema;
import java.util.Map;
import java.util.Set;
import org.springdoc.core.customizers.OpenApiCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class OpenApiConfig {
  @Bean
  OpenAPI recipesOpenApi() {
    return new OpenAPI()
        .info(
            new Info()
                .title("Recipes API")
                .version("0.0.1-SNAPSHOT")
                .description("API contract for the Recipes app"));
  }

  @Bean
  OpenApiCustomizer apiErrorSchemaCustomizer() {
    var apiErrorSchema = ModelConverters.getInstance().read(ApiError.class).get("ApiError");
    return openApi -> openApi.getComponents().addSchemas("ApiError", apiErrorSchema);
  }

  @Bean
  OpenApiCustomizer nullableReferenceSchemaCustomizer() {
    return OpenApiConfig::normalizeNullableReferences;
  }

  static void normalizeNullableReferences(OpenAPI openApi) {
    if (openApi.getComponents() == null || openApi.getComponents().getSchemas() == null) {
      return;
    }

    openApi.getComponents().getSchemas().values().forEach(OpenApiConfig::normalizeProperties);
  }

  private static void normalizeProperties(Schema<?> schema) {
    if (schema.getProperties() == null) {
      return;
    }

    for (Map.Entry<String, Schema> property : schema.getProperties().entrySet()) {
      Schema<?> propertySchema = property.getValue();
      boolean nullable =
          Boolean.TRUE.equals(propertySchema.getNullable())
              || "null".equals(propertySchema.getType())
              || propertySchema.getTypes() != null && propertySchema.getTypes().contains("null");
      if (nullable && propertySchema.get$ref() != null) {
        property.setValue(
            new ComposedSchema()
                .addOneOfItem(new Schema<>().$ref(propertySchema.get$ref()))
                .addOneOfItem(new Schema<>().types(Set.of("null"))));
      }
    }
  }
}
