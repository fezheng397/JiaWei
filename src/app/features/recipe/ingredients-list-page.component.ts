import { Component, computed, signal } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { AppIconComponent } from "../../shared/ui/app-icon.component";
import { ChefFilterComponent } from "./chef-filter.component";
import { INGREDIENT_VIEWS } from "./ingredient-view-model";
import { RECIPE_VIEWS } from "./recipe-view-model";

@Component({
  selector: "app-ingredients-list-page",
  imports: [
    RouterLink,
    RouterLinkActive,
    ChefFilterComponent,
    AppIconComponent,
  ],
  templateUrl: "./ingredients-list-page.component.html",
  styleUrl: "./recipe-display.css",
})
export class IngredientsListPageComponent {
  readonly recipes = RECIPE_VIEWS.filter((recipe) => recipe.kind === "dish");
  readonly ingredients = INGREDIENT_VIEWS.filter(
    (ingredient) => ingredient.madeByRecipe !== null,
  );
  readonly chefs = [
    ...new Set(
      this.ingredients.flatMap((ingredient) =>
        ingredient.madeByRecipe ? [ingredient.madeByRecipe.authorName] : [],
      ),
    ),
  ];
  readonly selectedChef = signal<string | null>(null);
  readonly filteredIngredients = computed(() => {
    const selectedChef = this.selectedChef();

    if (selectedChef === null) {
      return this.ingredients;
    }

    return this.ingredients.filter(
      (ingredient) => ingredient.madeByRecipe?.authorName === selectedChef,
    );
  });
  readonly filteredRecipeCount = computed(() => {
    const selectedChef = this.selectedChef();

    if (selectedChef === null) {
      return this.recipes.length;
    }

    return this.recipes.filter((recipe) => recipe.authorName === selectedChef)
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
