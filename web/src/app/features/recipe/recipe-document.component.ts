import { Component, Input } from "@angular/core";
import { RouterLink } from "@angular/router";
import { AppIconComponent } from "../../shared/icon/app-icon.component";
import { RecipeTimerComponent } from "./recipe-timer.component";
import type { RecipeView } from "./recipe-view-model";

@Component({
  selector: "app-recipe-document",
  imports: [RouterLink, RecipeTimerComponent, AppIconComponent],
  templateUrl: "./recipe-document.component.html",
  styleUrl: "./recipe-display.css",
})
export class RecipeDocumentComponent {
  @Input({ required: true }) recipe!: RecipeView;
  @Input({ required: true }) backLink!: string;
  @Input({ required: true }) backLabel!: string;
  @Input() title: string | null = null;
  @Input() usedInRecipes: readonly RecipeView[] = [];

  get displayTitle(): string {
    return this.title ?? this.recipe.name;
  }

  get secondaryStatLabel(): string {
    return this.recipe.kind === "ingredient" ? "Yield" : "Servings";
  }

  get secondaryStatValue(): string {
    if (this.recipe.kind === "ingredient") {
      return this.recipe.yieldAmount ?? "Not set";
    }

    return this.recipe.servings?.toString() ?? "Not set";
  }
}
