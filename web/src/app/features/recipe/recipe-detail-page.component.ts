import { Component, computed, inject } from "@angular/core";
import { rxResource, toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute } from "@angular/router";
import { RecipeDocumentComponent } from "./recipe-document.component";
import { RecipeService } from "./recipe.service";

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

  readonly recipeId = computed(
    () => this.paramMap().get("recipeId") ?? undefined,
  );
  readonly recipeResource = rxResource({
    params: () => this.recipeId(),
    stream: ({ params: recipeId }) =>
      this.recipeService.getRecipeDetailView(recipeId),
  });
}
