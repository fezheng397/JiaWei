import { inject } from "@angular/core";
import { CanActivateFn, Router, Routes } from "@angular/router";
import { IngredientDetailPageComponent } from "./features/recipe/ingredient-detail-page.component";
import { IngredientsListPageComponent } from "./features/recipe/ingredients-list-page.component";
import { RecipeDetailPageComponent } from "./features/recipe/recipe-detail-page.component";
import { RecipeService } from "./features/recipe/recipe.service";
import { RecipesListPageComponent } from "./features/recipe/recipes-list-page.component";
import { ShellComponent } from "./layout/shell/shell.component";

const redirectIngredientRecipes: CanActivateFn = (route) => {
  const ingredientId = inject(RecipeService).getIngredientRecipeRedirect(
    route.paramMap.get("recipeId"),
  );

  if (ingredientId !== null) {
    return inject(Router).createUrlTree(["/ingredients", ingredientId]);
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
