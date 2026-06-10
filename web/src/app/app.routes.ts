import { inject } from "@angular/core";
import { CanActivateFn, Router, Routes } from "@angular/router";
import { map } from "rxjs";
import { IngredientDetailPageComponent } from "./features/recipe/ingredient-detail-page.component";
import { IngredientsListPageComponent } from "./features/recipe/ingredients-list-page.component";
import { RecipeDetailPageComponent } from "./features/recipe/recipe-detail-page.component";
import { RecipeService } from "./features/recipe/recipe.service";
import { RecipesListPageComponent } from "./features/recipe/recipes-list-page.component";
import { RecipeCreatePageComponent } from "./features/recipe/create/recipe-create-page.component";
import { ShellComponent } from "./layout/shell/shell.component";

const redirectIngredientRecipes: CanActivateFn = (route) => {
  const recipeId = route.paramMap.get("recipeId");

  if (recipeId === null) {
    return true;
  }

  const router = inject(Router);
  return inject(RecipeService)
    .getIngredientRecipeRedirect(recipeId)
    .pipe(
      map((ingredientId) =>
        ingredientId === null
          ? true
          : router.createUrlTree(["/ingredients", ingredientId]),
      ),
    );
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
        path: "recipes/new",
        component: RecipeCreatePageComponent,
        title: "Create Recipe",
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
