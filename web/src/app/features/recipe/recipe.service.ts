import { inject, Injectable } from "@angular/core";
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
import { RecipeSummaryResponse } from "../../api/generated";
import { map, Observable } from "rxjs";
import { ApiService } from "../../core/api/api.service";

@Injectable({ providedIn: "root" })
export class RecipeService {
  private readonly apiService = inject(ApiService);

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

  getDishRecipeViews(): Observable<readonly RecipeView[]> {
    return this.apiService
      .get<RecipeSummaryResponse[]>("/recipes?kind=dish")
      .pipe(map((dtos) => dtos.map((dto) => this.recipeSummaryViewFor(dto))));
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

  private recipeSummaryViewFor(dto: RecipeSummaryResponse): RecipeView {
    return {
      id: dto.publicId,
      name: dto.name,
      description: dto.description,
      categoryLabel: dto.categoryLabel,
      authorName: dto.authorName,
      postedLabel: this.postedLabelFor(dto.publishedAt),
      kind: dto.kind,
      ingredientId: dto.ingredientPublicId,
      heroImageUrl: dto.heroImageUrl,
      prepTimeMinutes: null,
      cookTimeMinutes: null,
      totalTimeMinutes: dto.totalTimeMinutes,
      difficulty: dto.difficulty,
      difficultyLabel: this.difficultyLabelFor(dto.difficulty),
      tags: dto.tags,
      servings: dto.servings,
      yieldAmount: dto.yieldAmount,
      ingredients: [],
      steps: [],
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

  private difficultyLabelFor(
    difficulty: RecipeSummaryResponse["difficulty"],
  ): string | null {
    if (difficulty === null) {
      return null;
    }

    return difficulty[0].toUpperCase() + difficulty.slice(1);
  }

  private postedLabelFor(
    publishedAt: RecipeSummaryResponse["publishedAt"],
  ): string | null {
    if (publishedAt === null) {
      return null;
    }

    const publishedAtDate = new Date(publishedAt);
    const formatter = new Intl.RelativeTimeFormat("en", { numeric: "auto" });
    const daysSincePublished = Math.round(
      (publishedAtDate.getTime() - Date.now()) / (1000 * 60 * 60 * 24),
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
