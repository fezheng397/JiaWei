import { inject, Injectable } from "@angular/core";
import type { Id } from "../models/recipe";
import type {
  IngredientDetailView,
  RecipeView,
  StepIngredientView,
} from "../models/recipe-view-model";
import {
  AuthorControllerService,
  AuthorResponse,
  IngredientControllerService,
  IngredientResponse,
  IngredientLineResponse,
  IngredientRequest,
  RecipeControllerService,
  RecipeCreateRequest,
  RecipeResponse,
  RecipeSummaryResponse,
} from "../../../api/generated";
import { forkJoin, map, Observable, of, switchMap } from "rxjs";

@Injectable({ providedIn: "root" })
export class RecipeService {
  private readonly authorApi = inject(AuthorControllerService);
  private readonly ingredientApi = inject(IngredientControllerService);
  private readonly recipeApi = inject(RecipeControllerService);

  getDishRecipeViews(): Observable<readonly RecipeView[]> {
    return this.recipeApi
      .getRecipes("dish")
      .pipe(map((dtos) => dtos.map((dto) => this.recipeSummaryViewFor(dto))));
  }

  getRecipeDetailView(recipeId: Id): Observable<RecipeView> {
    return this.recipeApi
      .getRecipe(recipeId)
      .pipe(map((dto) => this.recipeDetailViewFor(dto)));
  }

  getIngredientsWithRecipes(): Observable<readonly IngredientDetailView[]> {
    return this.ingredientApi
      .getIngredients(true)
      .pipe(
        map((dtos) => dtos.map((dto) => this.ingredientSummaryViewFor(dto))),
      );
  }

  getIngredientDetailView(ingredientId: Id): Observable<IngredientDetailView> {
    return this.ingredientApi.getIngredient(ingredientId).pipe(
      switchMap((dto) => {
        const usedInRecipes = dto.usedInRecipes.map((recipe) =>
          this.recipeSummaryViewFor(recipe),
        );

        if (dto.madeByRecipe === null) {
          return of({
            id: dto.publicId,
            name: dto.name,
            madeByRecipe: null,
            usedInRecipes,
          });
        }

        return forkJoin({
          madeByRecipe: this.getRecipeDetailView(dto.madeByRecipe.publicId),
          usedInRecipes: of(usedInRecipes),
        }).pipe(
          map(({ madeByRecipe, usedInRecipes }) => ({
            id: dto.publicId,
            name: dto.name,
            madeByRecipe,
            usedInRecipes,
          })),
        );
      }),
    );
  }

  getIngredientRecipeRedirect(recipeId: Id): Observable<Id | null> {
    return this.recipeApi
      .getIngredientRedirect(recipeId)
      .pipe(map((response) => response?.ingredientPublicId ?? null));
  }

  getAuthors(): Observable<readonly AuthorResponse[]> {
    return this.authorApi.getAuthors();
  }

  getAllIngredients(): Observable<readonly IngredientResponse[]> {
    return this.ingredientApi.getIngredients(false);
  }

  createIngredient(request: IngredientRequest): Observable<IngredientResponse> {
    return this.ingredientApi.createIngredient(request);
  }

  createRecipe(request: RecipeCreateRequest): Observable<RecipeResponse> {
    return this.recipeApi.createRecipe(request);
  }

  private ingredientSummaryViewFor(
    dto: IngredientResponse,
  ): IngredientDetailView {
    return {
      id: dto.publicId,
      name: dto.name,
      madeByRecipe:
        dto.madeByRecipe === null
          ? null
          : this.recipeSummaryViewFor(dto.madeByRecipe),
      usedInRecipes: dto.usedInRecipes.map((recipe) =>
        this.recipeSummaryViewFor(recipe),
      ),
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

  private recipeDetailViewFor(dto: RecipeResponse): RecipeView {
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
      prepTimeMinutes: dto.prepTimeMinutes,
      cookTimeMinutes: dto.cookTimeMinutes,
      totalTimeMinutes: dto.totalTimeMinutes,
      difficulty: dto.difficulty,
      difficultyLabel: this.difficultyLabelFor(dto.difficulty),
      tags: dto.tags,
      servings: dto.servings,
      yieldAmount: dto.yieldAmount,
      ingredients: dto.ingredients.map((ingredient) =>
        this.ingredientLineViewFor(ingredient),
      ),
      steps: dto.steps.map((step) => ({
        id: step.publicId,
        position: step.position,
        instructions: step.instructions,
        timerMinutes: step.timerMinutes,
        usedIngredients: step.usedIngredients,
        ingredientDetails: step.ingredientDetails.map((ingredient) =>
          this.ingredientLineViewFor(ingredient),
        ),
      })),
    };
  }

  private ingredientLineViewFor(
    dto: IngredientLineResponse,
  ): StepIngredientView {
    return {
      id: dto.publicId,
      ingredientId: dto.ingredientPublicId,
      amount: dto.amount,
      name: dto.name,
      preparedByRecipeId: dto.preparedByRecipePublicId,
      preparedByRecipeName: dto.preparedByRecipeName,
    };
  }

  private difficultyLabelFor(
    difficulty: RecipeView["difficulty"],
  ): string | null {
    if (difficulty === null) {
      return null;
    }

    return difficulty[0].toUpperCase() + difficulty.slice(1);
  }

  private postedLabelFor(publishedAt: string | null): string | null {
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
}
