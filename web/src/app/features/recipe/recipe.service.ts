import { Injectable } from "@angular/core";
import {
  MOCK_AUTHORS,
  MOCK_INGREDIENTS,
  MOCK_RECIPES,
  MOCK_RECIPE_INGREDIENTS,
  MOCK_RECIPE_STEPS,
  MOCK_STEP_INGREDIENTS,
} from "./mock-recipe-data";
import type {
  Id,
  Ingredient,
  Recipe,
  RecipeIngredient,
  RecipeStep,
} from "./recipe";
import type {
  IngredientDetailView,
  RecipeView,
  StepIngredientView,
  StepView,
} from "./recipe-view-model";

@Injectable({ providedIn: "root" })
export class RecipeService {
  private readonly authorsById = new Map(
    MOCK_AUTHORS.map((author) => [author.id, author]),
  );
  private readonly recipesById = new Map(
    MOCK_RECIPES.map((recipe) => [recipe.id, recipe]),
  );
  private readonly ingredientsById = new Map(
    MOCK_INGREDIENTS.map((ingredient) => [ingredient.id, ingredient]),
  );
  private readonly recipeIngredientsById = new Map(
    MOCK_RECIPE_INGREDIENTS.map((ingredient) => [ingredient.id, ingredient]),
  );
  private readonly recipeViews = MOCK_RECIPES.map((recipe) =>
    this.recipeViewFor(recipe),
  );
  private readonly ingredientViews = MOCK_INGREDIENTS.map((ingredient) =>
    this.ingredientViewFor(ingredient),
  );

  getRecipeViews(): readonly RecipeView[] {
    return this.recipeViews;
  }

  getDishRecipeViews(): readonly RecipeView[] {
    return this.recipeViews.filter((recipe) => recipe.kind === "dish");
  }

  getRecipeView(recipeId: Id | null): RecipeView | undefined {
    if (recipeId === null) {
      return undefined;
    }

    return this.recipeViews.find((recipe) => recipe.id === recipeId);
  }

  getIngredientViews(): readonly IngredientDetailView[] {
    return this.ingredientViews;
  }

  getIngredientsWithRecipes(): readonly IngredientDetailView[] {
    return this.ingredientViews.filter(
      (ingredient) => ingredient.madeByRecipe !== null,
    );
  }

  getIngredientView(ingredientId: Id | null): IngredientDetailView | undefined {
    if (ingredientId === null) {
      return undefined;
    }

    return this.ingredientViews.find(
      (ingredient) => ingredient.id === ingredientId,
    );
  }

  getRecipesUsingIngredient(ingredientId: Id): readonly RecipeView[] {
    const usedInRecipeIds = [
      ...new Set(
        MOCK_RECIPE_INGREDIENTS.filter(
          (recipeIngredient) => recipeIngredient.ingredientId === ingredientId,
        ).map((recipeIngredient) => recipeIngredient.recipeId),
      ),
    ];

    return usedInRecipeIds.map((recipeId) =>
      this.required(
        this.getRecipeView(recipeId),
        `recipe using ingredient ${ingredientId}`,
      ),
    );
  }

  getIngredientRecipeRedirect(recipeId: Id | null): Id | null {
    const recipe =
      recipeId === null ? undefined : this.recipesById.get(recipeId);

    if (recipe?.kind !== "ingredient") {
      return null;
    }

    return recipe.ingredientId;
  }

  private ingredientViewFor(ingredient: Ingredient): IngredientDetailView {
    const producingRecipe = this.recipeViews.find(
      (recipe) =>
        recipe.kind === "ingredient" && recipe.ingredientId === ingredient.id,
    );

    return {
      id: ingredient.id,
      name: ingredient.name,
      madeByRecipe: producingRecipe ?? null,
      usedInRecipes: this.getRecipesUsingIngredient(ingredient.id).slice(0, 5),
    };
  }

  private recipeViewFor(recipe: Recipe): RecipeView {
    const author = this.required(
      this.authorsById.get(recipe.authorId),
      `author ${recipe.authorId}`,
    );
    const ingredients = MOCK_RECIPE_INGREDIENTS.filter(
      (ingredient) => ingredient.recipeId === recipe.id,
    )
      .sort((left, right) => left.position - right.position)
      .map((ingredient) => ({
        id: ingredient.id,
        ingredientId: ingredient.ingredientId,
        amount: this.amountFor(ingredient),
        name: this.required(
          this.ingredientsById.get(ingredient.ingredientId),
          `ingredient ${ingredient.ingredientId}`,
        ).name,
        preparedByRecipeId: ingredient.preparedByRecipeId,
        preparedByRecipeName: this.preparingRecipeNameFor(ingredient),
      }));
    const steps = MOCK_RECIPE_STEPS.filter(
      (step) => step.recipeId === recipe.id,
    )
      .sort((left, right) => left.position - right.position)
      .map((step) => this.stepViewFor(recipe, step));

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
      postedLabel: this.postedLabelFor(recipe),
      kind: recipe.kind,
      ingredientId: recipe.kind === "ingredient" ? recipe.ingredientId : null,
      heroImageUrl: recipe.heroImageUrl,
      prepTimeMinutes: recipe.prepTimeMinutes,
      cookTimeMinutes: recipe.cookTimeMinutes,
      totalTimeMinutes: this.totalTimeFor(recipe),
      difficulty: recipe.difficulty,
      difficultyLabel: this.difficultyLabelFor(recipe),
      tags: recipe.tags,
      servings: recipe.kind === "dish" ? recipe.servings : null,
      yieldAmount: this.yieldAmountFor(recipe),
      ingredients,
      steps,
    };
  }

  private stepViewFor(recipe: Recipe, step: RecipeStep): StepView {
    const ingredientDetails = MOCK_STEP_INGREDIENTS.filter(
      (link) => link.stepId === step.id,
    ).map<StepIngredientView>((link) => {
      const recipeIngredient = this.required(
        this.recipeIngredientsById.get(link.recipeIngredientId),
        `recipe ingredient ${link.recipeIngredientId}`,
      );

      if (recipeIngredient.recipeId !== recipe.id) {
        throw new Error(
          `Step ${step.id} links to another recipe's ingredient.`,
        );
      }

      const ingredient = this.required(
        this.ingredientsById.get(recipeIngredient.ingredientId),
        `ingredient ${recipeIngredient.ingredientId}`,
      );

      return {
        id: recipeIngredient.id,
        ingredientId: recipeIngredient.ingredientId,
        amount: this.amountFor(recipeIngredient),
        name: ingredient.name,
        preparedByRecipeId: recipeIngredient.preparedByRecipeId,
        preparedByRecipeName: this.preparingRecipeNameFor(recipeIngredient),
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

  private preparingRecipeNameFor(ingredient: RecipeIngredient): string | null {
    if (ingredient.preparedByRecipeId === null) {
      return null;
    }

    const recipe = this.required(
      this.recipesById.get(ingredient.preparedByRecipeId),
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

  private amountFor(ingredient: RecipeIngredient): string {
    return ingredient.unit
      ? `${ingredient.quantity} ${ingredient.unit}`
      : ingredient.quantity;
  }

  private totalTimeFor(recipe: Recipe): number | null {
    const times = [recipe.prepTimeMinutes, recipe.cookTimeMinutes].filter(
      (time): time is number => time !== null,
    );

    if (times.length === 0) {
      return null;
    }

    return times.reduce((total, time) => total + time, 0);
  }

  private yieldAmountFor(recipe: Recipe): string | null {
    if (recipe.kind !== "ingredient") {
      return null;
    }

    return recipe.yieldUnit
      ? `${recipe.yieldQuantity} ${recipe.yieldUnit}`
      : recipe.yieldQuantity;
  }

  private difficultyLabelFor(recipe: Recipe): string | null {
    if (recipe.difficulty === null) {
      return null;
    }

    return recipe.difficulty[0].toUpperCase() + recipe.difficulty.slice(1);
  }

  private postedLabelFor(recipe: Recipe): string | null {
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

  private required<T>(value: T | undefined, label: string): T {
    if (value === undefined) {
      throw new Error(`Mock recipe data is missing ${label}.`);
    }

    return value;
  }
}
