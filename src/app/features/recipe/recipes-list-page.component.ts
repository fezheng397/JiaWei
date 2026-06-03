import { Component, computed, signal } from "@angular/core";
import { RouterLink, RouterLinkActive } from "@angular/router";
import { ChefFilterComponent } from "./chef-filter.component";
import { INGREDIENT_VIEWS } from "./ingredient-view-model";
import { RECIPE_VIEWS } from "./recipe-view-model";
import { AppIconComponent } from "../../shared/ui/app-icon.component";

@Component({
  selector: "app-recipes-list-page",
  imports: [
    RouterLink,
    RouterLinkActive,
    ChefFilterComponent,
    AppIconComponent,
  ],
  templateUrl: "./recipes-list-page.component.html",
  styleUrl: "./recipe-display.css",
})
export class RecipesListPageComponent {
  readonly recipes = RECIPE_VIEWS.filter((recipe) => recipe.kind === "dish");
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
  readonly ingredientsWithRecipes = INGREDIENT_VIEWS.filter(
    (ingredient) => ingredient.madeByRecipe !== null,
  );
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
