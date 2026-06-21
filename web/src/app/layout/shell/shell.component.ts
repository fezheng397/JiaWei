import { Component } from "@angular/core";
import { RouterLink, RouterOutlet } from "@angular/router";
import { environment } from "../../../environments/environment";

@Component({
  selector: "app-shell",
  imports: [RouterLink, RouterOutlet],
  templateUrl: "./shell.component.html",
  styleUrl: "./shell.component.css",
})
export class ShellComponent {
  protected readonly showCreateAction = !environment.production;
}
