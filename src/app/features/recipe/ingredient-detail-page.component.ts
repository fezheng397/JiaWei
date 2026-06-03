import { Component, computed, inject } from "@angular/core";
import { toSignal } from "@angular/core/rxjs-interop";
import { ActivatedRoute, RouterLink } from "@angular/router";
import { INGREDIENT_VIEWS } from "./ingredient-view-model";
import { RecipeDocumentComponent } from "./recipe-document.component";

@Component({
  selector: "app-ingredient-detail-page",
  imports: [RecipeDocumentComponent, RouterLink],
  templateUrl: "./ingredient-detail-page.component.html",
  styleUrl: "./recipe-display.css",
})
export class IngredientDetailPageComponent {
  private readonly route = inject(ActivatedRoute);
  private readonly paramMap = toSignal(this.route.paramMap, {
    initialValue: this.route.snapshot.paramMap,
  });

  readonly ingredientId = computed(() => this.paramMap().get("ingredientId"));
  readonly ingredient = computed(() =>
    INGREDIENT_VIEWS.find(
      (ingredient) => ingredient.id === this.ingredientId(),
    ),
  );
}
