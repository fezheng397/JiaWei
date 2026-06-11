import { Component, Input } from "@angular/core";
import { RouterLink } from "@angular/router";
import { AppIconComponent } from "../../shared/icon/app-icon.component";

export interface RecipeCardView {
  title: string;
  description: string;
  authorName: string;
  heroImageUrl: string | null;
  route: string[];
  ariaLabel: string;
  detailsLabel: string;
  totalTimeMinutes: number | null;
  secondaryIcon: "users" | null;
  secondaryText: string | null;
}

@Component({
  selector: "app-recipe-card",
  imports: [RouterLink, AppIconComponent],
  templateUrl: "./recipe-card.component.html",
  styleUrl: "./recipe-display.css",
  host: {
    class: "recipe-card-host",
  },
})
export class RecipeCardComponent {
  @Input({ required: true }) card!: RecipeCardView;
}
