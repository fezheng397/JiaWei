import { Combobox, ComboboxInput } from "@angular/aria/combobox";
import {
  ChangeDetectionStrategy,
  Component,
  computed,
  input,
  model,
  output,
  signal,
  viewChild,
} from "@angular/core";
import type { FormValueControl } from "@angular/forms/signals";
import type { IngredientResponse } from "../../../api/generated";

let nextAutocompleteId = 0;

export type IngredientAutocompleteSelection =
  | {
      kind: "existing";
      ingredient: IngredientResponse;
    }
  | {
      kind: "new";
      name: string;
    };

@Component({
  selector: "app-ingredient-autocomplete",
  imports: [Combobox, ComboboxInput],
  templateUrl: "./ingredient-autocomplete.component.html",
  styleUrl: "./ingredient-autocomplete.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
})
export class IngredientAutocompleteComponent implements FormValueControl<string> {
  readonly value = model.required<string>();
  readonly options = input.required<readonly IngredientResponse[]>();
  readonly selectedPublicId = input("");
  readonly producedIngredient = input(false);
  readonly placeholder = input("Ingredient name");

  readonly selectionChange = output<IngredientAutocompleteSelection>();
  readonly textEdited = output<string>();

  readonly activeIndex = signal(-1);
  readonly isOpen = signal(false);
  readonly listboxId = `ingredient-autocomplete-${nextAutocompleteId++}`;
  readonly query = computed(() => this.value().trim());
  readonly normalizedQuery = computed(() => this.normalize(this.query()));
  readonly matchingOptions = computed(() => {
    const query = this.normalizedQuery();
    if (!query) {
      return [];
    }

    return this.options()
      .filter((option) => this.normalize(option.name).includes(query))
      .slice(0, 12);
  });
  readonly exactMatch = computed(() => {
    const query = this.normalizedQuery();
    return this.options().find(
      (option) => this.normalize(option.name) === query,
    );
  });
  readonly showCreateOption = computed(
    () => Boolean(this.query()) && !this.exactMatch(),
  );
  readonly activeOptionId = computed(() => {
    const selection = this.selectionAt(this.activeIndex());
    return selection ? `${this.listboxId}-${selection.key}` : null;
  });

  private readonly combobox = viewChild(Combobox);

  onInput(value: string): void {
    this.value.set(value);
    this.activeIndex.set(-1);
    this.textEdited.emit(value);

    const combobox = this.combobox();
    if (!value.trim()) {
      this.close();
      return;
    }
    this.isOpen.set(true);
    combobox?.open();
  }

  onKeydown(event: KeyboardEvent): void {
    if (event.key === "Escape") {
      this.close();
      return;
    }

    if (event.key === "ArrowDown" || event.key === "ArrowUp") {
      event.preventDefault();
      this.isOpen.set(true);
      this.combobox()?.open();
      this.moveActive(event.key === "ArrowDown" ? 1 : -1);
      return;
    }

    if (event.key === "Enter" && this.activeIndex() >= 0) {
      event.preventDefault();
      this.selectActive();
    }
  }

  onBlur(): void {
    setTimeout(() => this.close());
  }

  onSelection(values: string[]): void {
    const selected = values.at(-1);
    if (!selected) {
      return;
    }

    if (selected === "new") {
      const name = this.query();
      this.value.set(name);
      this.selectionChange.emit({ kind: "new", name });
    } else {
      const ingredient = this.options().find(
        (option) => option.publicId === selected,
      );
      if (!ingredient) {
        return;
      }
      this.value.set(ingredient.name);
      this.selectionChange.emit({ kind: "existing", ingredient });
    }

    this.close();
  }

  selectExisting(option: IngredientResponse): void {
    if (this.isDisabled(option)) {
      return;
    }
    this.onSelection([option.publicId]);
  }

  selectNew(): void {
    this.onSelection(["new"]);
  }

  isActive(option: IngredientResponse): boolean {
    return this.selectionAt(this.activeIndex())?.key === option.publicId;
  }

  isNewActive(): boolean {
    return this.selectionAt(this.activeIndex())?.key === "new";
  }

  isDisabled(option: IngredientResponse): boolean {
    return this.producedIngredient() && option.madeByRecipe !== null;
  }

  private moveActive(direction: 1 | -1): void {
    const selections = this.enabledSelections();
    if (!selections.length) {
      this.activeIndex.set(-1);
      return;
    }

    const current = this.activeIndex();
    const next =
      current < 0
        ? direction === 1
          ? 0
          : selections.length - 1
        : (current + direction + selections.length) % selections.length;
    this.activeIndex.set(next);
  }

  private selectActive(): void {
    const selection = this.selectionAt(this.activeIndex());
    if (!selection) {
      return;
    }
    selection.kind === "new"
      ? this.selectNew()
      : this.selectExisting(selection.option);
  }

  private selectionAt(
    index: number,
  ):
    | { kind: "existing"; key: string; option: IngredientResponse }
    | { kind: "new"; key: "new" }
    | undefined {
    return this.enabledSelections()[index];
  }

  private enabledSelections(): (
    | { kind: "existing"; key: string; option: IngredientResponse }
    | { kind: "new"; key: "new" }
  )[] {
    const selections: (
      | { kind: "existing"; key: string; option: IngredientResponse }
      | { kind: "new"; key: "new" }
    )[] = this.matchingOptions()
      .filter((option) => !this.isDisabled(option))
      .map((option) => ({
        kind: "existing" as const,
        key: option.publicId,
        option,
      }));

    if (this.showCreateOption()) {
      selections.push({ kind: "new", key: "new" });
    }
    return selections;
  }

  private normalize(value: string): string {
    return value.trim().replace(/\s+/g, " ").toLocaleLowerCase();
  }

  private close(): void {
    this.isOpen.set(false);
    this.activeIndex.set(-1);
    this.combobox()?.close();
  }
}
