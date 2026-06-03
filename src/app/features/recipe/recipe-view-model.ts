import {
  MOCK_AUTHORS,
  MOCK_INGREDIENTS,
  MOCK_RECIPES,
  MOCK_RECIPE_INGREDIENTS,
  MOCK_RECIPE_STEPS,
  MOCK_STEP_INGREDIENTS,
} from "./mock-recipe-data";
import type { Id, Recipe, RecipeIngredient, RecipeStep } from "./recipe";

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

const authorsById = new Map(MOCK_AUTHORS.map((author) => [author.id, author]));
const recipesById = new Map(MOCK_RECIPES.map((recipe) => [recipe.id, recipe]));
const ingredientsById = new Map(
  MOCK_INGREDIENTS.map((ingredient) => [ingredient.id, ingredient]),
);
const recipeIngredientsById = new Map(
  MOCK_RECIPE_INGREDIENTS.map((ingredient) => [ingredient.id, ingredient]),
);

function required<T>(value: T | undefined, label: string): T {
  if (value === undefined) {
    throw new Error(`Mock recipe data is missing ${label}.`);
  }

  return value;
}

function amountFor(ingredient: RecipeIngredient): string {
  return ingredient.unit
    ? `${ingredient.quantity} ${ingredient.unit}`
    : ingredient.quantity;
}

function preparingRecipeNameFor(ingredient: RecipeIngredient): string | null {
  if (ingredient.preparedByRecipeId === null) {
    return null;
  }

  const recipe = required(
    recipesById.get(ingredient.preparedByRecipeId),
    `preparation recipe ${ingredient.preparedByRecipeId}`,
  );

  if (
    recipe.kind !== "ingredient" ||
    recipe.ingredientId !== ingredient.ingredientId
  ) {
    throw new Error(
      `Recipe ${recipe.id} is not the ingredient recipe for ${ingredient.ingredientId}.`,
    );
  }

  return recipe.name;
}

function stepViewFor(recipe: Recipe, step: RecipeStep): StepView {
  const ingredientDetails = MOCK_STEP_INGREDIENTS.filter(
    (link) => link.stepId === step.id,
  ).map((link) => {
    const recipeIngredient = required(
      recipeIngredientsById.get(link.recipeIngredientId),
      `recipe ingredient ${link.recipeIngredientId}`,
    );

    if (recipeIngredient.recipeId !== recipe.id) {
      throw new Error(`Step ${step.id} links to another recipe's ingredient.`);
    }

    const ingredient = required(
      ingredientsById.get(recipeIngredient.ingredientId),
      `ingredient ${recipeIngredient.ingredientId}`,
    );

    return {
      id: recipeIngredient.id,
      ingredientId: recipeIngredient.ingredientId,
      amount: amountFor(recipeIngredient),
      name: ingredient.name,
      preparedByRecipeId: recipeIngredient.preparedByRecipeId,
      preparedByRecipeName: preparingRecipeNameFor(recipeIngredient),
    };
  });

  return {
    id: step.id,
    position: step.position,
    instructions: step.instructions,
    timerMinutes: step.timerMinutes,
    usedIngredients: ingredientDetails
      .map((ingredient) => ingredient.name)
      .join(", "),
    ingredientDetails,
  };
}

function totalTimeFor(recipe: Recipe): number | null {
  const times = [recipe.prepTimeMinutes, recipe.cookTimeMinutes].filter(
    (time): time is number => time !== null,
  );

  if (times.length === 0) {
    return null;
  }

  return times.reduce((total, time) => total + time, 0);
}

function yieldAmountFor(recipe: Recipe): string | null {
  if (recipe.kind !== "ingredient") {
    return null;
  }

  return recipe.yieldUnit
    ? `${recipe.yieldQuantity} ${recipe.yieldUnit}`
    : recipe.yieldQuantity;
}

function difficultyLabelFor(recipe: Recipe): string | null {
  if (recipe.difficulty === null) {
    return null;
  }

  return recipe.difficulty[0].toUpperCase() + recipe.difficulty.slice(1);
}

function postedLabelFor(recipe: Recipe): string | null {
  if (recipe.publishedAt === null) {
    return null;
  }

  const publishedAt = new Date(recipe.publishedAt);
  const formatter = new Intl.RelativeTimeFormat("en", { numeric: "auto" });
  const daysSincePublished = Math.round(
    (publishedAt.getTime() - Date.now()) / (1000 * 60 * 60 * 24),
  );

  return `Posted ${formatter.format(daysSincePublished, "day")}`;
}

function recipeViewFor(recipe: Recipe): RecipeView {
  const author = required(
    authorsById.get(recipe.authorId),
    `author ${recipe.authorId}`,
  );
  const ingredients = MOCK_RECIPE_INGREDIENTS.filter(
    (ingredient) => ingredient.recipeId === recipe.id,
  )
    .sort((left, right) => left.position - right.position)
    .map((ingredient) => ({
      id: ingredient.id,
      ingredientId: ingredient.ingredientId,
      amount: amountFor(ingredient),
      name: required(
        ingredientsById.get(ingredient.ingredientId),
        `ingredient ${ingredient.ingredientId}`,
      ).name,
      preparedByRecipeId: ingredient.preparedByRecipeId,
      preparedByRecipeName: preparingRecipeNameFor(ingredient),
    }));
  const steps = MOCK_RECIPE_STEPS.filter((step) => step.recipeId === recipe.id)
    .sort((left, right) => left.position - right.position)
    .map((step) => stepViewFor(recipe, step));

  return {
    id: recipe.id,
    name: recipe.name,
    description: recipe.description,
    categoryLabel:
      recipe.tags.length > 0
        ? recipe.tags[0]
        : recipe.kind === "dish"
          ? "Recipe"
          : "Ingredient",
    authorName: author.name,
    postedLabel: postedLabelFor(recipe),
    kind: recipe.kind,
    ingredientId: recipe.kind === "ingredient" ? recipe.ingredientId : null,
    heroImageUrl: recipe.heroImageUrl,
    prepTimeMinutes: recipe.prepTimeMinutes,
    cookTimeMinutes: recipe.cookTimeMinutes,
    totalTimeMinutes: totalTimeFor(recipe),
    difficulty: recipe.difficulty,
    difficultyLabel: difficultyLabelFor(recipe),
    tags: recipe.tags,
    servings: recipe.kind === "dish" ? recipe.servings : null,
    yieldAmount: yieldAmountFor(recipe),
    ingredients,
    steps,
  };
}

export const RECIPE_VIEWS = MOCK_RECIPES.map((recipe) => recipeViewFor(recipe));
