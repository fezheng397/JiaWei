import { Component, computed, inject } from "@angular/core";
import { rxResource, toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute } from "@angular/router";
import { RecipeDocumentComponent } from "../../components/recipe-document/recipe-document.component";
import { RecipeService } from "../../data-access/recipe.service";
import { AppButtonComponent } from "../../../../shared/button/app-button.component";

@Component({
  selector: "app-recipe-detail-page",
  imports: [AppButtonComponent, RecipeDocumentComponent],
  templateUrl: "./recipe-detail-page.component.html",
  styleUrl: "../../styles/recipe-page-shared.css",
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
