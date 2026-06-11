import { Component, Input, OnDestroy, computed, signal } from "@angular/core";
import { AppIconComponent } from "../../shared/icon/app-icon.component";

@Component({
  selector: "app-recipe-timer",
  imports: [AppIconComponent],
  template: `
    <div class="recipe-timer">
      <div class="timer-face" aria-hidden="true">
        <svg viewBox="0 0 48 48">
          <circle class="timer-track" cx="24" cy="24" r="20" />
          <circle
            class="timer-progress"
            cx="24"
            cy="24"
            r="20"
            [attr.stroke-dasharray]="circumference"
            [attr.stroke-dashoffset]="progressOffset()"
          />
        </svg>
        <span class="type-timer-face">{{ formattedTime() }}</span>
      </div>

      <div class="timer-actions">
        <button
          type="button"
          (click)="toggle()"
          [attr.aria-label]="isRunning() ? 'Pause timer' : 'Start timer'"
        >
          <app-icon [name]="isRunning() ? 'pause' : 'play'" [size]="16" />
        </button>
        <button type="button" (click)="reset()" aria-label="Reset timer">
          <app-icon name="reset" [size]="16" />
        </button>
      </div>

      <p class="type-ui-sm">
        {{ minutes }} {{ minutes === 1 ? "minute" : "minutes" }}
      </p>
    </div>
  `,
  styleUrl: "./recipe-timer.component.css",
})
export class RecipeTimerComponent implements OnDestroy {
  readonly circumference = 2 * Math.PI * 20;
  private intervalId: number | null = null;
  private totalSeconds = 0;

  readonly timeLeft = signal(0);
  readonly isRunning = signal(false);
  readonly formattedTime = computed(() => {
    const minutes = Math.floor(this.timeLeft() / 60);
    const seconds = this.timeLeft() % 60;

    return `${minutes}:${seconds.toString().padStart(2, "0")}`;
  });
  readonly progressOffset = computed(() => {
    if (this.totalSeconds === 0) {
      return this.circumference;
    }

    const progress = (this.totalSeconds - this.timeLeft()) / this.totalSeconds;

    return this.circumference * (1 - progress);
  });

  @Input({ required: true })
  set minutes(value: number) {
    this.totalSeconds = value * 60;
    this.reset();
  }

  get minutes(): number {
    return this.totalSeconds / 60;
  }

  toggle(): void {
    if (this.isRunning()) {
      this.pause();
      return;
    }

    this.isRunning.set(true);
    this.intervalId = window.setInterval(() => {
      this.timeLeft.update((current) => {
        if (current <= 1) {
          this.pause();
          return 0;
        }

        return current - 1;
      });
    }, 1000);
  }

  reset(): void {
    this.pause();
    this.timeLeft.set(this.totalSeconds);
  }

  ngOnDestroy(): void {
    this.pause();
  }

  private pause(): void {
    this.isRunning.set(false);

    if (this.intervalId !== null) {
      window.clearInterval(this.intervalId);
      this.intervalId = null;
    }
  }
}
