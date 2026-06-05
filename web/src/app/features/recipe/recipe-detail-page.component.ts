import { Component, computed, inject } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute } from "@angular/router";
import { map, switchMap } from "rxjs";
import { RecipeDocumentComponent } from "./recipe-document.component";
import { RecipeService } from "./recipe.service";
import type { RecipeView } from "./recipe-view-model";

@Component({
  selector: "app-recipe-detail-page",
  imports: [RecipeDocumentComponent],
  templateUrl: "./recipe-detail-page.component.html",
  styleUrl: "./recipe-display.css",
})
export class RecipeDetailPageComponent {
  private readonly recipeService = inject(RecipeService);
  private readonly route = inject(ActivatedRoute);
  private readonly paramMap = toSignal(this.route.paramMap, {
    initialValue: this.route.snapshot.paramMap,
  });
  readonly recipe = toSignal(
    this.route.paramMap.pipe(
      map((paramMap) => paramMap.get("recipeId")),
      switchMap((recipeId) => this.recipeService.getRecipeView(recipeId)),
    ),
    { initialValue: undefined as RecipeView | undefined },
  );

  readonly recipeId = computed(() => this.paramMap().get("recipeId"));
}
