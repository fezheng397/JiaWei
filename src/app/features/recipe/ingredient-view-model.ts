import { MOCK_INGREDIENTS, MOCK_RECIPE_INGREDIENTS } from "./mock-recipe-data";
import { RECIPE_VIEWS, type RecipeView } from "./recipe-view-model";
import type { Id, Ingredient } from "./recipe";

export interface IngredientDetailView {
  id: Id;
  name: string;
  madeByRecipe: RecipeView | null;
  usedInRecipes: readonly RecipeView[];
}

function required<T>(value: T | undefined, label: string): T {
  if (value === undefined) {
    throw new Error(`Mock ingredient data is missing ${label}.`);
  }

  return value;
}

function ingredientViewFor(ingredient: Ingredient): IngredientDetailView {
  const producingRecipe = RECIPE_VIEWS.find(
    (recipe) =>
      recipe.kind === "ingredient" && recipe.ingredientId === ingredient.id,
  );
  const usedInRecipeIds = [
    ...new Set(
      MOCK_RECIPE_INGREDIENTS.filter(
        (recipeIngredient) => recipeIngredient.ingredientId === ingredient.id,
      ).map((recipeIngredient) => recipeIngredient.recipeId),
    ),
  ];
  const usedInRecipes = usedInRecipeIds
    .map((recipeId) =>
      required(
        RECIPE_VIEWS.find((recipe) => recipe.id === recipeId),
        `recipe ${recipeId}`,
      ),
    )
    .slice(0, 5);

  return {
    id: ingredient.id,
    name: ingredient.name,
    madeByRecipe: producingRecipe ?? null,
    usedInRecipes,
  };
}

export const INGREDIENT_VIEWS = MOCK_INGREDIENTS.map((ingredient) =>
  ingredientViewFor(ingredient),
);
