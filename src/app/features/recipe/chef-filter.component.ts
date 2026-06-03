import {
  Component,
  ElementRef,
  EventEmitter,
  HostListener,
  Input,
  Output,
  inject,
  signal,
} from "@angular/core";
import { AppIconComponent } from "../../shared/ui/app-icon.component";

@Component({
  selector: "app-chef-filter",
  imports: [AppIconComponent],
  templateUrl: "./chef-filter.component.html",
  styleUrl: "./chef-filter.component.css",
})
export class ChefFilterComponent {
  private readonly elementRef = inject(ElementRef<HTMLElement>);

  @Input({ required: true }) chefs: readonly string[] = [];
  @Input() selectedChef: string | null = null;
  @Output() selectedChefChange = new EventEmitter<string | null>();

  readonly isOpen = signal(false);
  readonly search = signal("");

  get filteredChefs(): readonly string[] {
    const search = this.search().trim().toLowerCase();

    if (!search) {
      return this.chefs;
    }

    return this.chefs.filter((chef) => chef.toLowerCase().includes(search));
  }

  toggle(): void {
    this.isOpen.update((isOpen) => !isOpen);
  }

  updateSearch(event: Event): void {
    const input = event.target as HTMLInputElement;
    this.search.set(input.value);
  }

  selectChef(chef: string | null): void {
    this.selectedChefChange.emit(chef);
    this.search.set("");
    this.isOpen.set(false);
  }

  clear(event: MouseEvent): void {
    event.stopPropagation();
    this.selectChef(null);
  }

  @HostListener("document:mousedown", ["$event"])
  closeOnOutsideClick(event: MouseEvent): void {
    if (!this.elementRef.nativeElement.contains(event.target as Node)) {
      this.isOpen.set(false);
    }
  }
}
