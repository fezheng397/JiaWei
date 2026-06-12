import { Component, computed, inject, signal } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { ChefFilterComponent } from "./chef-filter.component";
import {
  RecipeCardComponent,
  type RecipeCardView,
} from "./recipe-card.component";
import { RecipeService } from "./recipe.service";
import { rxResource } from "@angular/core/rxjs-interop";
import { AppButtonComponent } from "../../shared/button/app-button.component";
import type { RecipeView } from "./recipe-view-model";

@Component({
  selector: "app-recipes-list-page",
  imports: [
    RouterLink,
    RouterLinkActive,
    AppButtonComponent,
    ChefFilterComponent,
    RecipeCardComponent,
  ],
  templateUrl: "./recipes-list-page.component.html",
  styleUrl: "./recipe-display.css",
})
export class RecipesListPageComponent {
  private readonly recipeService = inject(RecipeService);

  readonly recipesResource = rxResource({
    stream: () => this.recipeService.getDishRecipeViews(),
    defaultValue: [] as readonly RecipeView[],
  });

  readonly recipes = this.recipesResource.value;

  readonly chefs = computed(() => [
    ...new Set(this.recipes().map((recipe) => recipe.authorName)),
  ]);

  readonly selectedChef = signal<string | null>(null);

  readonly filteredRecipes = computed(() => {
    const selectedChef = this.selectedChef();
    const recipes = this.recipes();

    return selectedChef === null
      ? recipes
      : recipes.filter((recipe) => recipe.authorName === selectedChef);
  });

  readonly recipeCards = computed<RecipeCardView[]>(() =>
    this.filteredRecipes().map((recipe) => ({
      title: recipe.name,
      description: recipe.description,
      authorName: recipe.authorName,
      heroImageUrl: recipe.heroImageUrl,
      route: ["/recipe", recipe.id],
      ariaLabel: `View recipe for ${recipe.name}`,
      detailsLabel: "Recipe details",
      totalTimeMinutes: recipe.totalTimeMinutes,
      secondaryIcon: "users",
      secondaryText:
        recipe.servings === null ? null : recipe.servings.toString(),
    })),
  );
  readonly pageTitle = computed(() => {
    const selectedChef = this.selectedChef();

    return selectedChef ? `${selectedChef}'s Recipes` : "All Recipes";
  });
  readonly pageDescription = computed(() => {
    const selectedChef = this.selectedChef();

    return selectedChef
      ? `Browse all recipes and ingredients by ${selectedChef}`
      : "Home-cooked favorites from our community";
  });
}
