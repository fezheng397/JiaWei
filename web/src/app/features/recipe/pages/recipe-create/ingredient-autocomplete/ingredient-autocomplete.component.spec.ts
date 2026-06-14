import { ComponentFixture, TestBed } from "@angular/core/testing";
import {
  type IngredientResponse,
  RecipeSummaryResponseDifficultyEnum,
  RecipeSummaryResponseKindEnum,
} from "../../../../../api/generated";
import {
  IngredientAutocompleteComponent,
  type IngredientAutocompleteSelection,
} from "./ingredient-autocomplete.component";

describe("IngredientAutocompleteComponent", () => {
  let fixture: ComponentFixture<IngredientAutocompleteComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [IngredientAutocompleteComponent],
    }).compileComponents();

    fixture = TestBed.createComponent(IngredientAutocompleteComponent);
    fixture.componentRef.setInput("value", "");
    fixture.componentRef.setInput("options", options);
    await fixture.whenStable();
  });

  it("shows matching existing options and a create option", async () => {
    await typeQuery("po");

    expect(optionTexts()).toEqual([
      "RecipeHomemadeGroundPork",
      "IngredientPorkBelly",
      "NewCreate“po”",
    ]);
  });

  it("suppresses create for an exact existing match", async () => {
    await typeQuery("pork belly");

    expect(optionTexts()).toEqual(["IngredientPorkBelly"]);
  });

  it("shows the no-matches state with only a create option", async () => {
    await typeQuery("bok choy");

    expect(
      fixture.nativeElement.querySelector(".section-label").textContent.trim(),
    ).toBe("No matches");
    expect(optionTexts()).toEqual(["NewCreate“bokchoy”"]);
  });

  it("disables recipe-backed options for produced ingredients", async () => {
    fixture.componentRef.setInput("producedIngredient", true);
    await typeQuery("homemade ground pork");

    const recipeOption = fixture.nativeElement.querySelector(
      '.option[aria-disabled="true"]',
    ) as HTMLElement | null;
    expect(recipeOption?.textContent?.replace(/\s+/g, "")).toContain(
      "RecipeHomemadeGroundPorkAlreadyhasarecipe",
    );
    expect(optionTexts()).not.toContain("NewCreate“homemadegroundpork”");
  });

  it("marks the selected existing option", async () => {
    fixture.componentRef.setInput("selectedPublicId", "pork-belly-id");
    await typeQuery("pork belly");

    expect(fixture.nativeElement.querySelector(".check")).not.toBeNull();
    expect(
      fixture.nativeElement
        .querySelector('[role="option"]')
        .getAttribute("aria-selected"),
    ).toBe("true");
  });

  it("bounds large result sets", async () => {
    fixture.componentRef.setInput(
      "options",
      Array.from({ length: 20 }, (_, index) => ({
        publicId: `ingredient-${index}`,
        name: `Ingredient ${index}`,
        madeByRecipe: null,
        usedInRecipes: [],
      })),
    );
    await typeQuery("ingredient");

    expect(
      fixture.nativeElement.querySelectorAll('[role="option"]'),
    ).toHaveLength(13);
    expect(fixture.nativeElement.querySelector(".option-list")).not.toBeNull();
  });

  it("emits the selected recipe-backed ingredient", async () => {
    let selection: IngredientAutocompleteSelection | undefined;
    fixture.componentInstance.selectionChange.subscribe(
      (value) => (selection = value),
    );
    await typeQuery("ground");

    const option = fixture.nativeElement.querySelector(
      '.option:not([aria-disabled="true"])',
    ) as HTMLElement;
    option.dispatchEvent(new MouseEvent("mousedown", { bubbles: true }));
    await fixture.whenStable();

    expect(selection).toEqual({ kind: "existing", ingredient: options[0] });
  });

  it("emits a trimmed new ingredient selection", async () => {
    let selection: IngredientAutocompleteSelection | undefined;
    fixture.componentInstance.selectionChange.subscribe(
      (value) => (selection = value),
    );
    await typeQuery("  Bok Choy  ");

    (
      fixture.nativeElement.querySelector(".badge-new") as HTMLElement
    ).parentElement?.dispatchEvent(
      new MouseEvent("mousedown", { bubbles: true }),
    );
    await fixture.whenStable();

    expect(selection).toEqual({ kind: "new", name: "Bok Choy" });
  });

  it("supports keyboard selection", async () => {
    let selection: IngredientAutocompleteSelection | undefined;
    fixture.componentInstance.selectionChange.subscribe(
      (value) => (selection = value),
    );
    const input = await typeQuery("gar");

    input.dispatchEvent(
      new KeyboardEvent("keydown", {
        key: "ArrowDown",
        bubbles: true,
        cancelable: true,
      }),
    );
    input.dispatchEvent(
      new KeyboardEvent("keydown", {
        key: "Enter",
        bubbles: true,
        cancelable: true,
      }),
    );
    await fixture.whenStable();

    expect(selection).toEqual({ kind: "existing", ingredient: options[2] });
  });

  it("closes on escape", async () => {
    const input = await typeQuery("gar");

    input.dispatchEvent(
      new KeyboardEvent("keydown", {
        key: "Escape",
        bubbles: true,
      }),
    );
    await fixture.whenStable();

    expect(fixture.nativeElement.querySelector('[role="listbox"]')).toBeNull();
  });

  it("closes after the input loses focus", async () => {
    const input = await typeQuery("gar");

    input.dispatchEvent(new FocusEvent("blur"));
    await new Promise((resolve) => setTimeout(resolve));
    fixture.detectChanges();

    expect(fixture.nativeElement.querySelector('[role="listbox"]')).toBeNull();
  });

  async function typeQuery(query: string): Promise<HTMLInputElement> {
    const input = fixture.nativeElement.querySelector(
      "input",
    ) as HTMLInputElement;
    input.focus();
    input.value = query;
    input.dispatchEvent(new Event("input", { bubbles: true }));
    await fixture.whenStable();
    return input;
  }

  function optionTexts(): string[] {
    return Array.from(
      fixture.nativeElement.querySelectorAll(
        ".option",
      ) as NodeListOf<HTMLElement>,
    ).map((option) => option.textContent?.replace(/\s+/g, "") ?? "");
  }
});

const options: readonly IngredientResponse[] = [
  {
    publicId: "ground-pork-id",
    name: "Homemade Ground Pork",
    madeByRecipe: {
      publicId: "ground-pork-recipe-id",
      name: "Homemade Ground Pork",
      description: "Prepared pork.",
      categoryLabel: "Ingredient",
      authorName: "Jiawei",
      publishedAt: null,
      kind: RecipeSummaryResponseKindEnum.Ingredient,
      ingredientPublicId: "ground-pork-id",
      heroImageUrl: null,
      totalTimeMinutes: 10,
      difficulty: RecipeSummaryResponseDifficultyEnum.Easy,
      tags: [],
      servings: null,
      yieldAmount: "1 lb",
    },
    usedInRecipes: [],
  },
  {
    publicId: "pork-belly-id",
    name: "Pork Belly",
    madeByRecipe: null,
    usedInRecipes: [],
  },
  {
    publicId: "garlic-id",
    name: "Garlic",
    madeByRecipe: null,
    usedInRecipes: [],
  },
];
