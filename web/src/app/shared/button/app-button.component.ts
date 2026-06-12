import { NgTemplateOutlet } from "@angular/common";
import {
  ChangeDetectionStrategy,
  Component,
  booleanAttribute,
  computed,
  input,
} from "@angular/core";
import { RouterLink } from "@angular/router";

export type AppButtonVariant =
  | "unstyled"
  | "primary"
  | "secondary"
  | "ghost"
  | "icon";
export type AppButtonSize = "sm" | "md" | "lg";
export type AppButtonType = "button" | "submit" | "reset";

@Component({
  selector: "app-button",
  imports: [NgTemplateOutlet, RouterLink],
  template: `
    <ng-template #content><ng-content /></ng-template>

    @if (route(); as routeValue) {
      <a
        [routerLink]="routeValue"
        [attr.target]="target()"
        [attr.rel]="resolvedRel()"
        [attr.aria-label]="ariaLabel()"
        [attr.aria-disabled]="isUnavailable() || null"
        [attr.aria-busy]="loading() || null"
        [attr.tabindex]="isUnavailable() ? -1 : null"
        [class]="controlClasses()"
        (click)="handleAnchorClick($event)"
      >
        <ng-container [ngTemplateOutlet]="content" />
      </a>
    } @else if (href(); as hrefValue) {
      <a
        [href]="hrefValue"
        [attr.target]="target()"
        [attr.rel]="resolvedRel()"
        [attr.aria-label]="ariaLabel()"
        [attr.aria-disabled]="isUnavailable() || null"
        [attr.aria-busy]="loading() || null"
        [attr.tabindex]="isUnavailable() ? -1 : null"
        [class]="controlClasses()"
        (click)="handleAnchorClick($event)"
      >
        <ng-container [ngTemplateOutlet]="content" />
      </a>
    } @else {
      <button
        [type]="type()"
        [disabled]="isUnavailable()"
        [attr.aria-label]="ariaLabel()"
        [attr.aria-busy]="loading() || null"
        [class]="controlClasses()"
      >
        <ng-container [ngTemplateOutlet]="content" />
      </button>
    }
  `,
  styleUrl: "./app-button.component.css",
  changeDetection: ChangeDetectionStrategy.OnPush,
  host: {
    "[class.is-full-width]": "fullWidth()",
  },
})
export class AppButtonComponent {
  readonly variant = input<AppButtonVariant>("unstyled");
  readonly size = input<AppButtonSize>("md");
  readonly type = input<AppButtonType>("button");
  readonly route = input<string | readonly unknown[] | null>(null);
  readonly href = input<string | null>(null);
  readonly target = input<string | null>(null);
  readonly rel = input<string | null>(null);
  readonly disabled = input(false, { transform: booleanAttribute });
  readonly loading = input(false, { transform: booleanAttribute });
  readonly fullWidth = input(false, { transform: booleanAttribute });
  readonly ariaLabel = input<string | null>(null);
  readonly controlClassName = input("");

  readonly isUnavailable = computed(() => this.disabled() || this.loading());
  readonly resolvedRel = computed(() => {
    if (this.rel() !== null) {
      return this.rel();
    }
    return this.href() && this.target() === "_blank"
      ? "noreferrer noopener"
      : null;
  });
  readonly controlClasses = computed(() =>
    [
      "button-control",
      `button-control--${this.variant()}`,
      this.variant() === "unstyled" ? null : `button-control--${this.size()}`,
      this.fullWidth() ? "button-control--full-width" : null,
      this.controlClassName(),
    ]
      .filter(Boolean)
      .join(" "),
  );

  handleAnchorClick(event: MouseEvent): void {
    if (!this.isUnavailable()) {
      return;
    }

    event.preventDefault();
    event.stopPropagation();
  }
}
