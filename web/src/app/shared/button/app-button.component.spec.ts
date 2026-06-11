import { Component } from "@angular/core";
import { ComponentFixture, TestBed } from "@angular/core/testing";
import { provideRouter } from "@angular/router";
import { AppButtonComponent } from "./app-button.component";

describe("AppButtonComponent", () => {
  let fixture: ComponentFixture<AppButtonComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [AppButtonComponent, AppButtonRouterHostComponent],
      providers: [provideRouter([])],
    }).compileComponents();

    fixture = TestBed.createComponent(AppButtonComponent);
    await fixture.whenStable();
  });

  it("renders a safe unstyled button by default", () => {
    const button = control<HTMLButtonElement>("button");

    expect(button.type).toBe("button");
    expect(button.classList).toContain("button-control--unstyled");
  });

  it("renders configured button behavior and styling", async () => {
    fixture.componentRef.setInput("variant", "primary");
    fixture.componentRef.setInput("type", "submit");
    fixture.componentRef.setInput("controlClassName", "feature-action");
    await fixture.whenStable();

    const button = control<HTMLButtonElement>("button");
    expect(button.type).toBe("submit");
    expect(button.classList).toContain("button-control--primary");
    expect(button.classList).toContain("feature-action");
  });

  it("renders router navigation as an anchor", async () => {
    fixture.componentRef.setInput("route", ["/recipes"]);
    await fixture.whenStable();

    expect(control<HTMLAnchorElement>("a").getAttribute("href")).toBe(
      "/recipes",
    );
  });

  it("projects content into router navigation", async () => {
    const hostFixture = TestBed.createComponent(AppButtonRouterHostComponent);
    await hostFixture.whenStable();

    expect(
      hostFixture.nativeElement.querySelector("a").textContent.trim(),
    ).toBe("Cancel");
  });

  it("adds a safe default rel to external blank-target links", async () => {
    fixture.componentRef.setInput("href", "https://example.com");
    fixture.componentRef.setInput("target", "_blank");
    await fixture.whenStable();

    expect(control<HTMLAnchorElement>("a").rel).toBe("noreferrer noopener");
  });

  it("preserves an explicitly provided rel", async () => {
    fixture.componentRef.setInput("href", "https://example.com");
    fixture.componentRef.setInput("target", "_blank");
    fixture.componentRef.setInput("rel", "external");
    await fixture.whenStable();

    expect(control<HTMLAnchorElement>("a").rel).toBe("external");
  });

  it("disables buttons while loading", async () => {
    fixture.componentRef.setInput("loading", true);
    await fixture.whenStable();

    const button = control<HTMLButtonElement>("button");
    expect(button.disabled).toBe(true);
    expect(button.getAttribute("aria-busy")).toBe("true");
  });

  it("prevents disabled anchor navigation", async () => {
    fixture.componentRef.setInput("href", "https://example.com");
    fixture.componentRef.setInput("disabled", true);
    await fixture.whenStable();

    const anchor = control<HTMLAnchorElement>("a");
    const event = new MouseEvent("click", { bubbles: true, cancelable: true });
    anchor.dispatchEvent(event);

    expect(event.defaultPrevented).toBe(true);
    expect(anchor.getAttribute("aria-disabled")).toBe("true");
    expect(anchor.tabIndex).toBe(-1);
  });

  function control<T extends HTMLElement>(selector: string): T {
    return fixture.nativeElement.querySelector(selector) as T;
  }
});

@Component({
  imports: [AppButtonComponent],
  template: `
    <app-button variant="secondary" [route]="['/recipes']"> Cancel </app-button>
  `,
})
class AppButtonRouterHostComponent {}
