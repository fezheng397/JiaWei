export type Id = string;

export interface Author {
  id: Id;
  name: string;
}

export type RecipeDifficulty = "easy" | "medium" | "hard";

interface RecipeBase {
  id: Id;
  name: string;
  authorId: Id;
  description: string;
  publishedAt: string | null;
  heroImageUrl: string | null;
  prepTimeMinutes: number | null;
  cookTimeMinutes: number | null;
  difficulty: RecipeDifficulty | null;
  tags: readonly string[];
}

export interface DishRecipe extends RecipeBase {
  kind: "dish";
  servings: number | null;
}

export interface IngredientRecipe extends RecipeBase {
  kind: "ingredient";
  ingredientId: Id;
  yieldQuantity: string;
  yieldUnit: string | null;
}

export type Recipe = DishRecipe | IngredientRecipe;

export interface Ingredient {
  id: Id;
  name: string;
}

export interface RecipeIngredient {
  id: Id;
  recipeId: Id;
  ingredientId: Id;
  quantity: string;
  unit: string | null;
  position: number;
  preparedByRecipeId: Id | null;
}

export interface RecipeStep {
  id: Id;
  recipeId: Id;
  position: number;
  instructions: string;
  timerMinutes: number | null;
}

export interface StepIngredient {
  stepId: Id;
  recipeIngredientId: Id;
}
