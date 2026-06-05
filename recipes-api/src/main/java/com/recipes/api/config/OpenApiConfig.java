package com.recipes.api.config;

import com.recipes.api.common.ApiError;
import io.swagger.v3.core.converter.ModelConverters;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
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
}
