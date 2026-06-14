import { Component, computed, inject } from "@angular/core";
import { rxResource, toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { RecipeDocumentComponent } from "../../components/recipe-document/recipe-document.component";
import { RecipeService } from "../../data-access/recipe.service";
import { AppButtonComponent } from "../../../../shared/button/app-button.component";

@Component({
  selector: "app-ingredient-detail-page",
  imports: [AppButtonComponent, RecipeDocumentComponent, RouterLink],
  templateUrl: "./ingredient-detail-page.component.html",
  styleUrl: "../../styles/recipe-page-shared.css",
})
export class IngredientDetailPageComponent {
  private readonly recipeService = inject(RecipeService);
  private readonly route = inject(ActivatedRoute);
  private readonly paramMap = toSignal(this.route.paramMap, {
    initialValue: this.route.snapshot.paramMap,
  });

  readonly ingredientId = computed(
    () => this.paramMap().get("ingredientId") ?? undefined,
  );
  readonly ingredientResource = rxResource({
    params: () => this.ingredientId(),
    stream: ({ params: ingredientId }) =>
      this.recipeService.getIngredientDetailView(ingredientId),
  });
}
