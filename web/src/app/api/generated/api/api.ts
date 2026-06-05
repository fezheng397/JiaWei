export * from './ingredientController.service';
import { IngredientControllerService } from './ingredientController.service';
export * from './ingredientController.serviceInterface';
export * from './recipeController.service';
import { RecipeControllerService } from './recipeController.service';
export * from './recipeController.serviceInterface';
export const APIS = [IngredientControllerService, RecipeControllerService];
