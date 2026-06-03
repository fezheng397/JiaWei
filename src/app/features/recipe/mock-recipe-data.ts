import type {
  Author,
  Ingredient,
  Recipe,
  RecipeIngredient,
  RecipeStep,
  StepIngredient,
} from "./recipe";

export const MOCK_AUTHORS: readonly Author[] = [
  { id: "author-honglei", name: "Honglei Huang" },
];

export const MOCK_RECIPES: readonly Recipe[] = [
  {
    id: "recipe-ground-pork",
    name: "Homemade Ground Pork",
    authorId: "author-honglei",
    description:
      "A homemade ground pork blend that balances the richness of pork belly with the leanness of pork tenderloin.",
    publishedAt: "2026-06-01T12:00:00.000Z",
    heroImageUrl: null,
    prepTimeMinutes: 15,
    cookTimeMinutes: null,
    difficulty: "easy",
    tags: ["prep", "ingredient"],
    kind: "ingredient",
    ingredientId: "ingredient-ground-pork",
    yieldQuantity: "2",
    yieldUnit: "lb",
  },
  {
    id: "recipe-steamed-pork-eggs",
    name: "Steamed Pork and Eggs",
    authorId: "author-honglei",
    description:
      "A simple steamed dish with seasoned homemade ground pork, eggs, and scallions.",
    publishedAt: "2026-06-01T12:00:00.000Z",
    heroImageUrl: "/recipes/steamed-pork-and-eggs.png",
    prepTimeMinutes: 10,
    cookTimeMinutes: 20,
    difficulty: "easy",
    tags: ["steamed", "pork", "eggs"],
    kind: "dish",
    servings: 2,
  },
];

export const MOCK_INGREDIENTS: readonly Ingredient[] = [
  { id: "ingredient-ground-pork", name: "homemade ground pork" },
  { id: "ingredient-pork-belly", name: "pork belly" },
  { id: "ingredient-pork-tenderloin", name: "pork tenderloin" },
  { id: "ingredient-eggs", name: "eggs" },
  { id: "ingredient-scallions", name: "scallions" },
  { id: "ingredient-soy-sauce", name: "soy sauce" },
];

export const MOCK_RECIPE_INGREDIENTS: readonly RecipeIngredient[] = [
  {
    id: "line-ground-pork-belly",
    recipeId: "recipe-ground-pork",
    ingredientId: "ingredient-pork-belly",
    quantity: "1",
    unit: "lb",
    position: 1,
    preparedByRecipeId: null,
  },
  {
    id: "line-ground-pork-tenderloin",
    recipeId: "recipe-ground-pork",
    ingredientId: "ingredient-pork-tenderloin",
    quantity: "1",
    unit: "lb",
    position: 2,
    preparedByRecipeId: null,
  },
  {
    id: "line-steamed-pork-ground-pork",
    recipeId: "recipe-steamed-pork-eggs",
    ingredientId: "ingredient-ground-pork",
    quantity: "0.5",
    unit: "lb",
    position: 1,
    preparedByRecipeId: "recipe-ground-pork",
  },
  {
    id: "line-steamed-pork-eggs",
    recipeId: "recipe-steamed-pork-eggs",
    ingredientId: "ingredient-eggs",
    quantity: "4",
    unit: null,
    position: 2,
    preparedByRecipeId: null,
  },
  {
    id: "line-steamed-pork-soy-sauce",
    recipeId: "recipe-steamed-pork-eggs",
    ingredientId: "ingredient-soy-sauce",
    quantity: "to taste",
    unit: null,
    position: 3,
    preparedByRecipeId: null,
  },
  {
    id: "line-steamed-pork-scallions",
    recipeId: "recipe-steamed-pork-eggs",
    ingredientId: "ingredient-scallions",
    quantity: "to taste",
    unit: null,
    position: 4,
    preparedByRecipeId: null,
  },
];

export const MOCK_RECIPE_STEPS: readonly RecipeStep[] = [
  {
    id: "step-ground-pork-prep",
    recipeId: "recipe-ground-pork",
    position: 1,
    instructions:
      "Pat the pork belly and pork tenderloin dry with paper towels and rinse / clean them to remove blood (myoglobin).",
    timerMinutes: null,
  },
  {
    id: "step-ground-pork-grind",
    recipeId: "recipe-ground-pork",
    position: 2,
    instructions: "Grind the pork belly and pork tenderloin.",
    timerMinutes: null,
  },
  {
    id: "step-ground-pork-mix",
    recipeId: "recipe-ground-pork",
    position: 3,
    instructions:
      "Mix the ground meats together to create ground pork with balanced fat from the belly and tenderloin.",
    timerMinutes: null,
  },
  {
    id: "step-steamed-pork-season",
    recipeId: "recipe-steamed-pork-eggs",
    position: 1,
    instructions:
      "Mix the ground pork with soy sauce, stirring well so the meat absorbs the flavor.",
    timerMinutes: null,
  },
  {
    id: "step-steamed-pork-bowl",
    recipeId: "recipe-steamed-pork-eggs",
    position: 2,
    instructions: "Add the seasoned ground pork to a bowl.",
    timerMinutes: null,
  },
  {
    id: "step-steamed-pork-top",
    recipeId: "recipe-steamed-pork-eggs",
    position: 3,
    instructions:
      "Crack the eggs on top of the pork and scatter scallions over them.",
    timerMinutes: null,
  },
  {
    id: "step-steamed-pork-pot",
    recipeId: "recipe-steamed-pork-eggs",
    position: 4,
    instructions: "Fill the pot ~1/4 with water and place the bowl in it.",
    timerMinutes: null,
  },
  {
    id: "step-steamed-pork-cook",
    recipeId: "recipe-steamed-pork-eggs",
    position: 5,
    instructions: "Cover the pot and steam for 20 minutes.",
    timerMinutes: 20,
  },
];

export const MOCK_STEP_INGREDIENTS: readonly StepIngredient[] = [
  {
    stepId: "step-ground-pork-prep",
    recipeIngredientId: "line-ground-pork-belly",
  },
  {
    stepId: "step-ground-pork-prep",
    recipeIngredientId: "line-ground-pork-tenderloin",
  },
  {
    stepId: "step-ground-pork-grind",
    recipeIngredientId: "line-ground-pork-belly",
  },
  {
    stepId: "step-ground-pork-grind",
    recipeIngredientId: "line-ground-pork-tenderloin",
  },
  {
    stepId: "step-ground-pork-mix",
    recipeIngredientId: "line-ground-pork-belly",
  },
  {
    stepId: "step-ground-pork-mix",
    recipeIngredientId: "line-ground-pork-tenderloin",
  },
  {
    stepId: "step-steamed-pork-season",
    recipeIngredientId: "line-steamed-pork-ground-pork",
  },
  {
    stepId: "step-steamed-pork-season",
    recipeIngredientId: "line-steamed-pork-soy-sauce",
  },
  {
    stepId: "step-steamed-pork-bowl",
    recipeIngredientId: "line-steamed-pork-ground-pork",
  },
  {
    stepId: "step-steamed-pork-top",
    recipeIngredientId: "line-steamed-pork-eggs",
  },
  {
    stepId: "step-steamed-pork-top",
    recipeIngredientId: "line-steamed-pork-scallions",
  },
];
