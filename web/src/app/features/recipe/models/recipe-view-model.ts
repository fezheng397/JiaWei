import type { Id, Recipe } from "./recipe";

export interface IngredientView {
  id: Id;
  ingredientId: Id;
  amount: string;
  name: string;
  preparedByRecipeId: Id | null;
  preparedByRecipeName: string | null;
}

export interface StepIngredientView {
  id: Id;
  ingredientId: Id;
  amount: string;
  name: string;
  preparedByRecipeId: Id | null;
  preparedByRecipeName: string | null;
}

export interface StepView {
  id: Id;
  position: number;
  instructions: string;
  timerMinutes: number | null;
  usedIngredients: string;
  ingredientDetails: readonly StepIngredientView[];
}

export interface RecipeView {
  id: Id;
  name: string;
  description: string;
  categoryLabel: string;
  authorName: string;
  postedLabel: string | null;
  kind: Recipe["kind"];
  ingredientId: Id | null;
  heroImageUrl: string | null;
  prepTimeMinutes: number | null;
  cookTimeMinutes: number | null;
  totalTimeMinutes: number | null;
  difficulty: Recipe["difficulty"];
  difficultyLabel: string | null;
  tags: readonly string[];
  servings: number | null;
  yieldAmount: string | null;
  ingredients: readonly IngredientView[];
  steps: readonly StepView[];
}

export interface IngredientDetailView {
  id: Id;
  name: string;
  madeByRecipe: RecipeView | null;
  usedInRecipes: readonly RecipeView[];
}
