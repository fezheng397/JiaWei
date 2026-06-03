import { Component, computed, inject, signal } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { ChefFilterComponent } from "./chef-filter.component";
import {
  RecipeCardComponent,
  type RecipeCardView,
} from "./recipe-card.component";
import { RecipeService } from "./recipe.service";

@Component({
  selector: "app-recipes-list-page",
  imports: [
    RouterLink,
    RouterLinkActive,
    ChefFilterComponent,
    RecipeCardComponent,
  ],
  templateUrl: "./recipes-list-page.component.html",
  styleUrl: "./recipe-display.css",
})
export class RecipesListPageComponent {
  private readonly recipeService = inject(RecipeService);

  readonly recipes = this.recipeService.getDishRecipeViews();
  readonly chefs = [
    ...new Set(this.recipes.map((recipe) => recipe.authorName)),
  ];
  readonly selectedChef = signal<string | null>(null);
  readonly filteredRecipes = computed(() => {
    const selectedChef = this.selectedChef();

    if (selectedChef === null) {
      return this.recipes;
    }

    return this.recipes.filter((recipe) => recipe.authorName === selectedChef);
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
  readonly ingredientsWithRecipes =
    this.recipeService.getIngredientsWithRecipes();
  readonly filteredIngredientCount = computed(() => {
    const selectedChef = this.selectedChef();

    if (selectedChef === null) {
      return this.ingredientsWithRecipes.length;
    }

    return this.ingredientsWithRecipes.filter(
      (ingredient) => ingredient.madeByRecipe?.authorName === selectedChef,
    ).length;
  });
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
