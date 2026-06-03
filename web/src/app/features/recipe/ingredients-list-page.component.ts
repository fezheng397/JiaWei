import { Component, computed, inject, signal } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { ChefFilterComponent } from "./chef-filter.component";
import {
  RecipeCardComponent,
  type RecipeCardView,
} from "./recipe-card.component";
import { RecipeService } from "./recipe.service";
import type { IngredientDetailView, RecipeView } from "./recipe-view-model";

@Component({
  selector: "app-ingredients-list-page",
  imports: [
    RouterLink,
    RouterLinkActive,
    ChefFilterComponent,
    RecipeCardComponent,
  ],
  templateUrl: "./ingredients-list-page.component.html",
  styleUrl: "./recipe-display.css",
})
export class IngredientsListPageComponent {
  private readonly recipeService = inject(RecipeService);

  readonly recipes = toSignal(this.recipeService.getDishRecipeViews(), {
    initialValue: [] as readonly RecipeView[],
  });
  readonly ingredients = toSignal(
    this.recipeService.getIngredientsWithRecipes(),
    {
      initialValue: [] as readonly IngredientDetailView[],
    },
  );
  readonly chefs = computed(() => [
    ...new Set(
      this.ingredients().flatMap((ingredient) =>
        ingredient.madeByRecipe ? [ingredient.madeByRecipe.authorName] : [],
      ),
    ),
  ]);
  readonly selectedChef = signal<string | null>(null);
  readonly filteredIngredients = computed(() => {
    const selectedChef = this.selectedChef();

    if (selectedChef === null) {
      return this.ingredients();
    }

    return this.ingredients().filter(
      (ingredient) => ingredient.madeByRecipe?.authorName === selectedChef,
    );
  });
  readonly ingredientCards = computed<RecipeCardView[]>(() =>
    this.filteredIngredients().flatMap((ingredient) => {
      const recipe = ingredient.madeByRecipe;

      if (recipe === null) {
        return [];
      }

      return [
        {
          title: ingredient.name,
          description: recipe.description,
          authorName: recipe.authorName,
          heroImageUrl: recipe.heroImageUrl,
          route: ["/ingredients", ingredient.id],
          ariaLabel: `View ingredient for ${ingredient.name}`,
          detailsLabel: "Ingredient recipe details",
          totalTimeMinutes: recipe.totalTimeMinutes,
          secondaryIcon: null,
          secondaryText: recipe.yieldAmount,
        },
      ];
    }),
  );
  readonly filteredRecipeCount = computed(() => {
    const selectedChef = this.selectedChef();

    if (selectedChef === null) {
      return this.recipes().length;
    }

    return this.recipes().filter((recipe) => recipe.authorName === selectedChef)
      .length;
  });
  readonly pageTitle = computed(() => {
    const selectedChef = this.selectedChef();

    return selectedChef ? `${selectedChef}'s Ingredients` : "All Ingredients";
  });
  readonly pageDescription = computed(() => {
    const selectedChef = this.selectedChef();

    return selectedChef
      ? `Browse all recipes and ingredients by ${selectedChef}`
      : "Homemade building blocks with recipes for preparing them.";
  });
}
