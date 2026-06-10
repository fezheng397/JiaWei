import type { RecipeCreateRequest } from "../../../api/generated";

export type RecipeCreateKind = "dish" | "ingredient";
export type RecipeCreateDifficulty = "" | "easy" | "medium" | "hard";

export interface RecipeIngredientFormValue {
  clientRef: string;
  name: string;
  ingredientPublicId: string;
  quantity: string;
  unit: string;
  usePreparedRecipe: boolean;
  preparedByRecipePublicId: string;
}

export interface RecipeStepFormValue {
  instructions: string;
  timerMinutes: number;
  ingredientLineRefs: string[];
}

export interface RecipeCreateFormValue {
  kind: RecipeCreateKind;
  name: string;
  description: string;
  authorPublicId: string;
  tags: string[];
  prepTimeMinutes: number;
  cookTimeMinutes: number;
  difficulty: RecipeCreateDifficulty;
  servings: number;
  producedIngredientName: string;
  producedIngredientPublicId: string;
  yieldQuantity: string;
  yieldUnit: string;
  ingredients: RecipeIngredientFormValue[];
  steps: RecipeStepFormValue[];
}

export function toRecipeCreateRequest(
  value: RecipeCreateFormValue,
): RecipeCreateRequest {
  const request: RecipeCreateRequest = {
    kind: value.kind as RecipeCreateRequest["kind"],
    name: value.name.trim(),
    description: value.description.trim(),
    authorPublicId: value.authorPublicId,
    tags: value.tags,
    ingredients: value.ingredients.map((ingredient) => ({
      clientRef: ingredient.clientRef,
      ingredientPublicId: ingredient.ingredientPublicId,
      quantity: ingredient.quantity.trim(),
      ...(textOrUndefined(ingredient.unit)
        ? { unit: textOrUndefined(ingredient.unit) }
        : {}),
      ...(ingredient.usePreparedRecipe &&
      textOrUndefined(ingredient.preparedByRecipePublicId)
        ? {
            preparedByRecipePublicId: textOrUndefined(
              ingredient.preparedByRecipePublicId,
            ),
          }
        : {}),
    })),
    steps: value.steps.map((step) => ({
      instructions: step.instructions.trim(),
      ...(positiveOrUndefined(step.timerMinutes)
        ? { timerMinutes: positiveOrUndefined(step.timerMinutes) }
        : {}),
      ingredientLineRefs: step.ingredientLineRefs,
    })),
    ...(positiveOrUndefined(value.prepTimeMinutes)
      ? { prepTimeMinutes: positiveOrUndefined(value.prepTimeMinutes) }
      : {}),
    ...(positiveOrUndefined(value.cookTimeMinutes)
      ? { cookTimeMinutes: positiveOrUndefined(value.cookTimeMinutes) }
      : {}),
    ...(value.difficulty
      ? { difficulty: value.difficulty as RecipeCreateRequest["difficulty"] }
      : {}),
  };

  if (value.kind === "dish") {
    if (positiveOrUndefined(value.servings)) {
      request.servings = positiveOrUndefined(value.servings);
    }
  } else {
    request.producedIngredientPublicId = value.producedIngredientPublicId;
    request.yieldQuantity = value.yieldQuantity.trim();
    if (textOrUndefined(value.yieldUnit)) {
      request.yieldUnit = textOrUndefined(value.yieldUnit);
    }
  }

  return request;
}

function positiveOrUndefined(value: number): number | undefined {
  return value > 0 ? value : undefined;
}

function textOrUndefined(value: string): string | undefined {
  const trimmed = value.trim();
  return trimmed ? trimmed : undefined;
}
