import { Component, computed, inject } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { map, switchMap } from "rxjs";
import { RecipeDocumentComponent } from "./recipe-document.component";
import { RecipeService } from "./recipe.service";
import type { IngredientDetailView } from "./recipe-view-model";

@Component({
  selector: "app-ingredient-detail-page",
  imports: [RecipeDocumentComponent, RouterLink],
  templateUrl: "./ingredient-detail-page.component.html",
  styleUrl: "./recipe-display.css",
})
export class IngredientDetailPageComponent {
  private readonly recipeService = inject(RecipeService);
  private readonly route = inject(ActivatedRoute);
  private readonly paramMap = toSignal(this.route.paramMap, {
    initialValue: this.route.snapshot.paramMap,
  });
  readonly ingredient = toSignal(
    this.route.paramMap.pipe(
      map((paramMap) => paramMap.get("ingredientId")),
      switchMap((ingredientId) =>
        this.recipeService.getIngredientView(ingredientId),
      ),
    ),
    { initialValue: undefined as IngredientDetailView | undefined },
  );

  readonly ingredientId = computed(() => this.paramMap().get("ingredientId"));
}
