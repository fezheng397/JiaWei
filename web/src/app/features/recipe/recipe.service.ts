import { HttpClient, HttpParams } from "@angular/common/http";
import { Injectable, inject } from "@angular/core";
import { catchError, map, of, switchMap, type Observable } from "rxjs";
import type { Id, Recipe } from "./recipe";
import type {
  IngredientDetailView,
  IngredientView,
  RecipeView,
  StepIngredientView,
  StepView,
} from "./recipe-view-model";

type ApiRecipeKind = Recipe["kind"];
type ApiRecipeDifficulty = Recipe["difficulty"];

interface RecipeSummaryResponse {
  publicId: Id;
  name: string;
  description: string;
  categoryLabel: string;
  authorName: string;
  publishedAt: string | null;
  kind: ApiRecipeKind;
  ingredientPublicId: Id | null;
  heroImageUrl: string | null;
  totalTimeMinutes: number | null;
  difficulty: ApiRecipeDifficulty;
  tags: string[];
  servings: number | null;
  yieldAmount: string | null;
}

interface RecipeResponse extends RecipeSummaryResponse {
  prepTimeMinutes: number | null;
  cookTimeMinutes: number | null;
  ingredients: IngredientLineResponse[];
  steps: StepResponse[];
}

interface IngredientLineResponse {
  publicId: Id;
  ingredientPublicId: Id;
  amount: string;
  name: string;
  preparedByRecipePublicId: Id | null;
  preparedByRecipeName: string | null;
}

interface StepResponse {
  publicId: Id;
  position: number;
  instructions: string;
  timerMinutes: number | null;
  usedIngredients: string;
  ingredientDetails: IngredientLineResponse[];
}

interface IngredientResponse {
  publicId: Id;
  name: string;
  madeByRecipe: RecipeSummaryResponse | null;
  usedInRecipes: RecipeSummaryResponse[];
}

interface RecipeIngredientRedirectResponse {
  ingredientPublicId: Id;
}

@Injectable({ providedIn: "root" })
export class RecipeService {
  private readonly http = inject(HttpClient);
  private readonly apiBaseUrl = this.apiOrigin();

  getDishRecipeViews(): Observable<readonly RecipeView[]> {
    return this.getRecipeViews("dish");
  }

  getRecipeViews(kind?: ApiRecipeKind): Observable<readonly RecipeView[]> {
    const options =
      kind === undefined ? {} : { params: new HttpParams().set("kind", kind) };

    return this.http
      .get<RecipeSummaryResponse[]>(`${this.apiBaseUrl}/recipes`, options)
      .pipe(
        map((recipes) => recipes.map((recipe) => this.summaryView(recipe))),
      );
  }

  getRecipeView(recipeId: Id | null): Observable<RecipeView | undefined> {
    if (recipeId === null) {
      return of(undefined);
    }

    return this.http
      .get<RecipeResponse>(`${this.apiBaseUrl}/recipes/${recipeId}`)
      .pipe(
        map((recipe) => this.recipeView(recipe)),
        catchError(() => of(undefined)),
      );
  }

  getIngredientsWithRecipes(): Observable<readonly IngredientDetailView[]> {
    return this.getIngredientViews(true);
  }

  getIngredientViews(
    hasRecipe?: boolean,
  ): Observable<readonly IngredientDetailView[]> {
    const options =
      hasRecipe === undefined
        ? {}
        : { params: new HttpParams().set("hasRecipe", hasRecipe) };

    return this.http
      .get<IngredientResponse[]>(`${this.apiBaseUrl}/ingredients`, options)
      .pipe(
        map((ingredients) =>
          ingredients.map((ingredient) => this.ingredientView(ingredient)),
        ),
      );
  }

  getIngredientView(
    ingredientId: Id | null,
  ): Observable<IngredientDetailView | undefined> {
    if (ingredientId === null) {
      return of(undefined);
    }

    return this.http
      .get<IngredientResponse>(`${this.apiBaseUrl}/ingredients/${ingredientId}`)
      .pipe(
        switchMap((ingredient) => {
          if (ingredient.madeByRecipe === null) {
            return of(this.ingredientView(ingredient));
          }

          const madeByRecipeSummary = ingredient.madeByRecipe;

          return this.getRecipeView(madeByRecipeSummary.publicId).pipe(
            map((madeByRecipe) => ({
              id: ingredient.publicId,
              name: ingredient.name,
              madeByRecipe:
                madeByRecipe ?? this.summaryView(madeByRecipeSummary),
              usedInRecipes: ingredient.usedInRecipes.map((recipe) =>
                this.summaryView(recipe),
              ),
            })),
          );
        }),
        catchError(() => of(undefined)),
      );
  }

  getIngredientRecipeRedirect(recipeId: Id | null): Observable<Id | null> {
    if (recipeId === null) {
      return of(null);
    }

    return this.http
      .get<RecipeIngredientRedirectResponse>(
        `${this.apiBaseUrl}/recipes/${recipeId}/ingredient-redirect`,
      )
      .pipe(
        map((response) => response.ingredientPublicId),
        catchError(() => of(null)),
      );
  }

  private ingredientView(ingredient: IngredientResponse): IngredientDetailView {
    return {
      id: ingredient.publicId,
      name: ingredient.name,
      madeByRecipe:
        ingredient.madeByRecipe === null
          ? null
          : this.summaryView(ingredient.madeByRecipe),
      usedInRecipes: ingredient.usedInRecipes.map((recipe) =>
        this.summaryView(recipe),
      ),
    };
  }

  private recipeView(recipe: RecipeResponse): RecipeView {
    return {
      ...this.summaryView(recipe),
      prepTimeMinutes: recipe.prepTimeMinutes,
      cookTimeMinutes: recipe.cookTimeMinutes,
      ingredients: recipe.ingredients.map((ingredient) =>
        this.ingredientLineView(ingredient),
      ),
      steps: recipe.steps.map((step) => this.stepView(step)),
    };
  }

  private summaryView(recipe: RecipeSummaryResponse): RecipeView {
    return {
      id: recipe.publicId,
      name: recipe.name,
      description: recipe.description,
      categoryLabel: recipe.categoryLabel,
      authorName: recipe.authorName,
      postedLabel: this.postedLabelFor(recipe.publishedAt),
      kind: recipe.kind,
      ingredientId: recipe.ingredientPublicId,
      heroImageUrl: recipe.heroImageUrl,
      prepTimeMinutes: null,
      cookTimeMinutes: null,
      totalTimeMinutes: recipe.totalTimeMinutes,
      difficulty: recipe.difficulty,
      difficultyLabel: this.difficultyLabelFor(recipe.difficulty),
      tags: recipe.tags,
      servings: recipe.servings,
      yieldAmount: recipe.yieldAmount,
      ingredients: [],
      steps: [],
    };
  }

  private stepView(step: StepResponse): StepView {
    return {
      id: step.publicId,
      position: step.position,
      instructions: step.instructions,
      timerMinutes: step.timerMinutes,
      usedIngredients: step.usedIngredients,
      ingredientDetails: step.ingredientDetails.map((ingredient) =>
        this.stepIngredientLineView(ingredient),
      ),
    };
  }

  private ingredientLineView(
    ingredient: IngredientLineResponse,
  ): IngredientView {
    return {
      id: ingredient.publicId,
      ingredientId: ingredient.ingredientPublicId,
      amount: ingredient.amount,
      name: ingredient.name,
      preparedByRecipeId: ingredient.preparedByRecipePublicId,
      preparedByRecipeName: ingredient.preparedByRecipeName,
    };
  }

  private stepIngredientLineView(
    ingredient: IngredientLineResponse,
  ): StepIngredientView {
    return {
      id: ingredient.publicId,
      ingredientId: ingredient.ingredientPublicId,
      amount: ingredient.amount,
      name: ingredient.name,
      preparedByRecipeId: ingredient.preparedByRecipePublicId,
      preparedByRecipeName: ingredient.preparedByRecipeName,
    };
  }

  private difficultyLabelFor(difficulty: ApiRecipeDifficulty): string | null {
    if (difficulty === null) {
      return null;
    }

    return difficulty[0].toUpperCase() + difficulty.slice(1);
  }

  private postedLabelFor(publishedAt: string | null): string | null {
    if (publishedAt === null) {
      return null;
    }

    const date = new Date(publishedAt);

    if (Number.isNaN(date.getTime())) {
      return null;
    }

    const formatter = new Intl.RelativeTimeFormat("en", { numeric: "auto" });
    const daysSincePublished = Math.round(
      (date.getTime() - Date.now()) / (1000 * 60 * 60 * 24),
    );

    return `Posted ${formatter.format(daysSincePublished, "day")}`;
  }

  private apiOrigin(): string {
    if (
      globalThis.location.hostname === "localhost" ||
      globalThis.location.hostname === "127.0.0.1"
    ) {
      return "http://localhost:8080";
    }

    return "https://api.jiawei.app";
  }
}
