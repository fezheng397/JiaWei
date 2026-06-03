import { Component } from "@angular/core";
import { RouterLink, RouterOutlet } from "@angular/router";

@Component({
  selector: "app-shell",
  imports: [RouterLink, RouterOutlet],
  templateUrl: "./shell.component.html",
  styleUrl: "./shell.component.css",
})
export class ShellComponent {}
