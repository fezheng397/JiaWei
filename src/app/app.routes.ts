import { inject } from "@angular/core";
import { CanActivateFn, Router, Routes } from "@angular/router";
import { IngredientDetailPageComponent } from "./features/recipe/ingredient-detail-page.component";
import { IngredientsListPageComponent } from "./features/recipe/ingredients-list-page.component";
import { MOCK_RECIPES } from "./features/recipe/mock-recipe-data";
import { RecipeDetailPageComponent } from "./features/recipe/recipe-detail-page.component";
import { RecipesListPageComponent } from "./features/recipe/recipes-list-page.component";
import { ShellComponent } from "./layout/shell/shell.component";

const redirectIngredientRecipes: CanActivateFn = (route) => {
  const recipe = MOCK_RECIPES.find(
    (recipe) => recipe.id === route.paramMap.get("recipeId"),
  );

  if (recipe?.kind === "ingredient") {
    return inject(Router).createUrlTree(["/ingredients", recipe.ingredientId]);
  }

  return true;
};

export const routes: Routes = [
  {
    path: "",
    component: ShellComponent,
    children: [
      { path: "", pathMatch: "full", redirectTo: "recipes" },
      {
        path: "recipes",
        component: RecipesListPageComponent,
        title: "Recipes",
      },
      {
        path: "recipe/:recipeId",
        component: RecipeDetailPageComponent,
        title: "Recipe",
        canActivate: [redirectIngredientRecipes],
      },
      {
        path: "ingredients",
        component: IngredientsListPageComponent,
        title: "Ingredients",
      },
      {
        path: "ingredients/:ingredientId",
        component: IngredientDetailPageComponent,
        title: "Ingredient",
      },
    ],
  },
  { path: "**", redirectTo: "recipes" },
];
