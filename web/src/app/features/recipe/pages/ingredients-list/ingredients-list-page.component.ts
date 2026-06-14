import { Component, computed, inject, signal } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { ChefFilterComponent } from "../../components/chef-filter/chef-filter.component";
import {
  RecipeCardComponent,
  type RecipeCardView,
} from "../../components/recipe-card/recipe-card.component";
import { RecipeService } from "../../data-access/recipe.service";
import { rxResource } from "@angular/core/rxjs-interop";
import { AppButtonComponent } from "../../../../shared/button/app-button.component";
import type { IngredientDetailView } from "../../models/recipe-view-model";

@Component({
  selector: "app-ingredients-list-page",
  imports: [
    RouterLink,
    RouterLinkActive,
    AppButtonComponent,
    ChefFilterComponent,
    RecipeCardComponent,
  ],
  templateUrl: "./ingredients-list-page.component.html",
  styleUrl: "../../styles/recipe-page-shared.css",
})
export class IngredientsListPageComponent {
  private readonly recipeService = inject(RecipeService);

  readonly ingredientsResource = rxResource({
    stream: () => this.recipeService.getIngredientsWithRecipes(),
    defaultValue: [] as readonly IngredientDetailView[],
  });
  readonly ingredients = this.ingredientsResource.value;
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
