package com.recipes.api;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class RecipesApiApplication {
  public static void main(String[] args) {
    SpringApplication.run(RecipesApiApplication.class, args);
  }
}
