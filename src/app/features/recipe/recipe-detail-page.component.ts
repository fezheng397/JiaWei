import { Component, computed, inject } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute } from "@angular/router";
import { RecipeDocumentComponent } from "./recipe-document.component";
import { RECIPE_VIEWS } from "./recipe-view-model";

@Component({
  selector: "app-recipe-detail-page",
  imports: [RecipeDocumentComponent],
  templateUrl: "./recipe-detail-page.component.html",
  styleUrl: "./recipe-display.css",
})
export class RecipeDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly paramMap = toSignal(this.route.paramMap, {
    initialValue: this.route.snapshot.paramMap,
  });

  readonly recipeId = computed(() => this.paramMap().get("recipeId"));
  readonly recipe = computed(() =>
    RECIPE_VIEWS.find((recipe) => recipe.id === this.recipeId()),
  );
}
