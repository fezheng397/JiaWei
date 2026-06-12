import { HttpErrorResponse } from "@angular/common/http";
import { Component, computed, inject, signal } from "@angular/core";
import { rxResource } from "@angular/core/rxjs-interop";
import {
  applyEach,
  form,
  FormField,
  minLength,
  required,
  submit,
} from "@angular/forms/signals";
import { Router, RouterLink } from "@angular/router";
import { firstValueFrom } from "rxjs";
import type { ApiError, IngredientResponse } from "../../../api/generated";
import { AppButtonComponent } from "../../../shared/button/app-button.component";
import { RecipeService } from "../recipe.service";
import {
  IngredientAutocompleteComponent,
  type IngredientAutocompleteSelection,
} from "./ingredient-autocomplete.component";
import {
  type RecipeCreateFormValue,
  type RecipeIngredientFormValue,
  type RecipeStepFormValue,
  toRecipeCreateRequest,
} from "./recipe-create-form";

@Component({
  selector: "app-recipe-create-page",
  imports: [
    AppButtonComponent,
    FormField,
    IngredientAutocompleteComponent,
    RouterLink,
  ],
  templateUrl: "./recipe-create-page.component.html",
  styleUrl: "./recipe-create-page.component.css",
})
export class RecipeCreatePageComponent {
  private readonly recipeService = inject(RecipeService);
  private readonly router = inject(Router);
  private nextRef = 1;

  readonly authorsResource = rxResource({
    stream: () => this.recipeService.getAuthors(),
    defaultValue: [],
  });
  readonly ingredientsResource = rxResource({
    stream: () => this.recipeService.getAllIngredients(),
    defaultValue: [] as readonly IngredientResponse[],
  });
  readonly authors = computed(() => {
    const authors = this.authorsResource.value();
    return Array.isArray(authors) ? authors : [];
  });
  readonly ingredients = computed(() => {
    const ingredients = this.ingredientsResource.value();
    return Array.isArray(ingredients) ? ingredients : [];
  });
  readonly submitting = signal(false);
  readonly submitError = signal("");
  readonly tagDraft = signal("");

  readonly model = signal<RecipeCreateFormValue>({
    kind: "dish",
    name: "",
    description: "",
    authorPublicId: "",
    tags: [],
    prepTimeMinutes: 0,
    cookTimeMinutes: 0,
    difficulty: "",
    servings: 0,
    producedIngredientName: "",
    producedIngredientPublicId: "",
    yieldQuantity: "",
    yieldUnit: "",
    ingredients: [this.newIngredient()],
    steps: [this.newStep()],
  });

  readonly recipeForm = form(this.model, (path) => {
    required(path.name, { message: "Recipe title is required." });
    required(path.description, { message: "Description is required." });
    required(path.authorPublicId, { message: "Choose an author." });
    minLength(path.tags, 1, { message: "Add at least one tag." });
    minLength(path.ingredients, 1, { message: "Add at least one ingredient." });
    minLength(path.steps, 1, { message: "Add at least one step." });
    required(path.producedIngredientName, {
      message: "Choose the ingredient this recipe produces.",
      when: ({ valueOf }) => valueOf(path.kind) === "ingredient",
    });
    required(path.yieldQuantity, {
      message: "Yield quantity is required.",
      when: ({ valueOf }) => valueOf(path.kind) === "ingredient",
    });
    applyEach(path.ingredients, (ingredient) => {
      required(ingredient.name, { message: "Ingredient name is required." });
      required(ingredient.quantity, { message: "Quantity is required." });
    });
    applyEach(path.steps, (step) => {
      required(step.instructions, {
        message: "Step instructions are required.",
      });
    });
  });

  readonly isIngredientRecipe = computed(
    () => this.model().kind === "ingredient",
  );

  setKind(kind: RecipeCreateFormValue["kind"]): void {
    this.model.update((value) => ({ ...value, kind }));
  }

  addTag(): void {
    const tag = this.tagDraft().trim();
    if (!tag) {
      return;
    }

    this.model.update((value) => ({
      ...value,
      tags: value.tags.includes(tag) ? value.tags : [...value.tags, tag],
    }));
    this.tagDraft.set("");
  }

  removeTag(tag: string): void {
    this.model.update((value) => ({
      ...value,
      tags: value.tags.filter((existing) => existing !== tag),
    }));
  }

  onTagKeydown(event: KeyboardEvent): void {
    if (event.key !== "Enter" && event.key !== ",") {
      return;
    }
    event.preventDefault();
    this.addTag();
  }

  addIngredient(): void {
    this.model.update((value) => ({
      ...value,
      ingredients: [...value.ingredients, this.newIngredient()],
    }));
  }

  removeIngredient(index: number): void {
    const removedRef = this.model().ingredients[index]?.clientRef;
    this.model.update((value) => ({
      ...value,
      ingredients: value.ingredients.filter(
        (_, itemIndex) => itemIndex !== index,
      ),
      steps: value.steps.map((step) => ({
        ...step,
        ingredientLineRefs: step.ingredientLineRefs.filter(
          (ref) => ref !== removedRef,
        ),
      })),
    }));
  }

  addStep(): void {
    this.model.update((value) => ({
      ...value,
      steps: [...value.steps, this.newStep()],
    }));
  }

  removeStep(index: number): void {
    this.model.update((value) => ({
      ...value,
      steps: value.steps.filter((_, itemIndex) => itemIndex !== index),
    }));
  }

  toggleStepIngredient(stepIndex: number, clientRef: string): void {
    this.model.update((value) => ({
      ...value,
      steps: value.steps.map((step, index) => {
        if (index !== stepIndex) {
          return step;
        }
        return {
          ...step,
          ingredientLineRefs: step.ingredientLineRefs.includes(clientRef)
            ? step.ingredientLineRefs.filter((ref) => ref !== clientRef)
            : [...step.ingredientLineRefs, clientRef],
        };
      }),
    }));
  }

  ingredientForName(name: string): IngredientResponse | undefined {
    const normalized = this.normalizeName(name);
    return this.ingredients().find(
      (ingredient) => this.normalizeName(ingredient.name) === normalized,
    );
  }

  onIngredientTextEdited(index: number): void {
    this.model.update((value) => ({
      ...value,
      ingredients: value.ingredients.map((row, itemIndex) =>
        itemIndex === index
          ? {
              ...row,
              ingredientPublicId: "",
              usePreparedRecipe: false,
              preparedByRecipePublicId: "",
            }
          : row,
      ),
    }));
  }

  onIngredientSelection(
    index: number,
    selection: IngredientAutocompleteSelection,
  ): void {
    this.model.update((value) => ({
      ...value,
      ingredients: value.ingredients.map((row, itemIndex) => {
        if (itemIndex !== index) {
          return row;
        }

        if (selection.kind === "new") {
          return {
            ...row,
            name: selection.name,
            ingredientPublicId: "",
            usePreparedRecipe: false,
            preparedByRecipePublicId: "",
          };
        }

        const preparedRecipe = selection.ingredient.madeByRecipe;
        return {
          ...row,
          name: selection.ingredient.name,
          ingredientPublicId: selection.ingredient.publicId,
          usePreparedRecipe: preparedRecipe !== null,
          preparedByRecipePublicId: preparedRecipe?.publicId ?? "",
        };
      }),
    }));
  }

  onProducedIngredientTextEdited(): void {
    this.model.update((value) => ({
      ...value,
      producedIngredientPublicId: "",
    }));
  }

  onProducedIngredientSelection(
    selection: IngredientAutocompleteSelection,
  ): void {
    this.model.update((value) => ({
      ...value,
      producedIngredientName:
        selection.kind === "new" ? selection.name : selection.ingredient.name,
      producedIngredientPublicId:
        selection.kind === "new" ? "" : selection.ingredient.publicId,
    }));
  }

  setPreparedRecipe(index: number, event: Event): void {
    const checked = (event.target as HTMLInputElement).checked;
    const row = this.model().ingredients[index];
    const ingredient = this.ingredientForName(row.name);
    this.model.update((value) => ({
      ...value,
      ingredients: value.ingredients.map((item, itemIndex) =>
        itemIndex === index
          ? {
              ...item,
              usePreparedRecipe: checked,
              preparedByRecipePublicId:
                checked && ingredient?.madeByRecipe
                  ? ingredient.madeByRecipe.publicId
                  : "",
            }
          : item,
      ),
    }));
  }

  onSubmit(event: Event): void {
    event.preventDefault();
    this.submitError.set("");
    void submit(this.recipeForm, async () => {
      this.submitting.set(true);
      try {
        await this.resolveIngredients();
        const created = await firstValueFrom(
          this.recipeService.createRecipe(toRecipeCreateRequest(this.model())),
        );
        await this.router.navigate(
          created.kind === "ingredient" && created.ingredientPublicId
            ? ["/ingredients", created.ingredientPublicId]
            : ["/recipe", created.publicId],
        );
      } catch (error) {
        this.submitError.set(this.errorMessage(error));
      } finally {
        this.submitting.set(false);
      }
    });
  }

  private async resolveIngredients(): Promise<void> {
    const ingredientRows = [...this.model().ingredients];
    for (let index = 0; index < ingredientRows.length; index++) {
      const row = ingredientRows[index];
      const ingredient = await this.resolveIngredient(row.name);
      this.updateResolvedIngredient(index, ingredient);
    }

    if (this.model().kind === "ingredient") {
      const produced = await this.resolveIngredient(
        this.model().producedIngredientName,
      );
      this.model.update((value) => ({
        ...value,
        producedIngredientPublicId: produced.publicId,
      }));
    }
  }

  private async resolveIngredient(name: string): Promise<IngredientResponse> {
    const existing = this.ingredientForName(name);
    if (existing) {
      return existing;
    }

    try {
      const created = await firstValueFrom(
        this.recipeService.createIngredient({ name: name.trim() }),
      );
      this.ingredientsResource.reload();
      return created;
    } catch (error) {
      if (!(error instanceof HttpErrorResponse) || error.status !== 409) {
        throw error;
      }

      const refreshed = await firstValueFrom(
        this.recipeService.getAllIngredients(),
      );
      const normalized = this.normalizeName(name);
      const duplicate = refreshed.find(
        (ingredient) => this.normalizeName(ingredient.name) === normalized,
      );
      if (!duplicate) {
        throw error;
      }
      this.ingredientsResource.reload();
      return duplicate;
    }
  }

  private updateResolvedIngredient(
    index: number,
    ingredient: IngredientResponse,
  ): void {
    this.model.update((value) => ({
      ...value,
      ingredients: value.ingredients.map((row, itemIndex) =>
        itemIndex === index
          ? {
              ...row,
              name: ingredient.name,
              ingredientPublicId: ingredient.publicId,
              preparedByRecipePublicId:
                row.usePreparedRecipe && ingredient.madeByRecipe
                  ? ingredient.madeByRecipe.publicId
                  : "",
            }
          : row,
      ),
    }));
  }

  private newIngredient(): RecipeIngredientFormValue {
    return {
      clientRef: `ingredient-${this.nextRef++}`,
      name: "",
      ingredientPublicId: "",
      quantity: "",
      unit: "",
      usePreparedRecipe: false,
      preparedByRecipePublicId: "",
    };
  }

  private newStep(): RecipeStepFormValue {
    return { instructions: "", timerMinutes: 0, ingredientLineRefs: [] };
  }

  private normalizeName(name: string): string {
    return name.trim().replace(/\s+/g, " ").toLocaleLowerCase();
  }

  private errorMessage(error: unknown): string {
    if (error instanceof HttpErrorResponse) {
      const apiError = error.error as Partial<ApiError> | undefined;
      return apiError?.message ?? "The recipe could not be created.";
    }
    return "The recipe could not be created.";
  }
}
