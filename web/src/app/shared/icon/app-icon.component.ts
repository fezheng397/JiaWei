import { Component, Input } from "@angular/core";

export type AppIconName =
  | "book-open"
  | "check"
  | "chevron-down"
  | "clock"
  | "leaf"
  | "pause"
  | "play"
  | "plus"
  | "reset"
  | "search"
  | "users"
  | "x";

@Component({
  selector: "app-icon",
  template: `
    <svg
      aria-hidden="true"
      fill="none"
      stroke="currentColor"
      stroke-linecap="round"
      stroke-linejoin="round"
      stroke-width="2"
      viewBox="0 0 24 24"
      [attr.width]="size"
      [attr.height]="size"
    >
      @switch (name) {
        @case ("book-open") {
          <path d="M12 7v14" />
          <path
            d="M3 18a1 1 0 0 1-1-1V5a1 1 0 0 1 1-1h5a4 4 0 0 1 4 4 4 4 0 0 1 4-4h5a1 1 0 0 1 1 1v12a1 1 0 0 1-1 1h-5a4 4 0 0 0-4 4 4 4 0 0 0-4-4H3Z"
          />
        }
        @case ("check") {
          <path d="m20 6-11 11-5-5" />
        }
        @case ("chevron-down") {
          <path d="m6 9 6 6 6-6" />
        }
        @case ("clock") {
          <circle cx="12" cy="12" r="10" />
          <path d="M12 6v6l4 2" />
        }
        @case ("leaf") {
          <path
            d="M11 20A7 7 0 0 1 9.8 6.1C15.5 5 19 2 20 2c0 1-.5 8-4.9 11.7C12.8 15.6 11 17.5 11 20Z"
          />
          <path d="M2 21c0-3 1.85-5.36 5.08-6.94C9.4 12.92 12.32 12 16 12" />
        }
        @case ("pause") {
          <path d="M8 5v14" />
          <path d="M16 5v14" />
        }
        @case ("play") {
          <path d="m6 3 14 9-14 9V3Z" />
        }
        @case ("plus") {
          <path d="M5 12h14" />
          <path d="M12 5v14" />
        }
        @case ("reset") {
          <path d="M3 12a9 9 0 1 0 3-6.7" />
          <path d="M3 3v6h6" />
        }
        @case ("search") {
          <circle cx="11" cy="11" r="8" />
          <path d="m21 21-4.3-4.3" />
        }
        @case ("users") {
          <path d="M16 21v-2a4 4 0 0 0-4-4H6a4 4 0 0 0-4 4v2" />
          <circle cx="9" cy="7" r="4" />
          <path d="M22 21v-2a4 4 0 0 0-3-3.87" />
          <path d="M16 3.13a4 4 0 0 1 0 7.75" />
        }
        @case ("x") {
          <path d="M18 6 6 18" />
          <path d="m6 6 12 12" />
        }
      }
    </svg>
  `,
  styles: [
    `
      :host {
        display: inline-flex;
        flex: none;
        line-height: 0;
      }

      svg {
        display: block;
      }
    `,
  ],
})
export class AppIconComponent {
  @Input({ required: true }) name!: AppIconName;
  @Input() size = 16;
}
