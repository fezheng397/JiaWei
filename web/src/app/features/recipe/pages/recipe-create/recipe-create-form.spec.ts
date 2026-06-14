import {
  type RecipeCreateFormValue,
  toRecipeCreateRequest,
} from "./recipe-create-form";

describe("toRecipeCreateRequest", () => {
  it("maps a dish and preserves stable ingredient references", () => {
    const request = toRecipeCreateRequest(
      formValue({
        kind: "dish",
        servings: 4,
        ingredients: [
          {
            clientRef: "ingredient-7",
            name: "Salt",
            ingredientPublicId: "salt-id",
            quantity: "1",
            unit: "tsp",
            usePreparedRecipe: false,
            preparedByRecipePublicId: "",
          },
        ],
        steps: [
          {
            instructions: "Season.",
            timerMinutes: 0,
            ingredientLineRefs: ["ingredient-7"],
          },
        ],
      }),
    );

    expect(request.servings).toBe(4);
    expect(request.producedIngredientPublicId).toBeUndefined();
    expect(request.ingredients[0].clientRef).toBe("ingredient-7");
    expect(request.steps[0].ingredientLineRefs).toEqual(["ingredient-7"]);
  });

  it("maps ingredient-recipe fields and omits dish-only servings", () => {
    const request = toRecipeCreateRequest(
      formValue({
        kind: "ingredient",
        servings: 8,
        producedIngredientPublicId: "broth-id",
        yieldQuantity: "2",
        yieldUnit: "cups",
      }),
    );

    expect(request.servings).toBeUndefined();
    expect(request.producedIngredientPublicId).toBe("broth-id");
    expect(request.yieldQuantity).toBe("2");
    expect(request.yieldUnit).toBe("cups");
  });

  it("only includes a prepared recipe when the row opts into it", () => {
    const request = toRecipeCreateRequest(
      formValue({
        ingredients: [
          {
            clientRef: "ingredient-1",
            name: "Broth",
            ingredientPublicId: "broth-id",
            quantity: "1",
            unit: "",
            usePreparedRecipe: true,
            preparedByRecipePublicId: "broth-recipe-id",
          },
          {
            clientRef: "ingredient-2",
            name: "Salt",
            ingredientPublicId: "salt-id",
            quantity: "1",
            unit: "",
            usePreparedRecipe: false,
            preparedByRecipePublicId: "ignored-id",
          },
        ],
      }),
    );

    expect(request.ingredients[0].preparedByRecipePublicId).toBe(
      "broth-recipe-id",
    );
    expect(request.ingredients[1].preparedByRecipePublicId).toBeUndefined();
  });
});

function formValue(
  overrides: Partial<RecipeCreateFormValue> = {},
): RecipeCreateFormValue {
  return {
    kind: "dish",
    name: "Test Recipe",
    description: "A useful test recipe.",
    authorPublicId: "author-id",
    tags: ["test"],
    prepTimeMinutes: 5,
    cookTimeMinutes: 10,
    difficulty: "easy",
    servings: 2,
    producedIngredientName: "",
    producedIngredientPublicId: "",
    yieldQuantity: "",
    yieldUnit: "",
    ingredients: [
      {
        clientRef: "ingredient-1",
        name: "Salt",
        ingredientPublicId: "salt-id",
        quantity: "1",
        unit: "tsp",
        usePreparedRecipe: false,
        preparedByRecipePublicId: "",
      },
    ],
    steps: [
      {
        instructions: "Cook.",
        timerMinutes: 5,
        ingredientLineRefs: ["ingredient-1"],
      },
    ],
    ...overrides,
  };
}
