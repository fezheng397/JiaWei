-- Constraint values must match RecipeDifficulty and RecipeKind enums.
comment on constraint recipes_difficulty_check on recipes is 'Must match RecipeDifficulty enum values.';
comment on constraint recipes_kind_check on recipes is 'Must match RecipeKind enum values.';

alter table recipe_tags
  drop column if exists id,
  drop column if exists public_id,
  drop column if exists created_at,
  drop column if exists updated_at,
  drop column if exists created_by,
  drop column if exists updated_by,
  drop column if exists version;

alter table recipe_tags
  drop constraint if exists recipe_tags_pkey;

alter table recipe_tags
  add primary key (recipe_id, position);

create index if not exists idx_recipe_tags_recipe_id on recipe_tags(recipe_id);

alter table step_ingredients
  drop column if exists id,
  drop column if exists public_id,
  drop column if exists created_at,
  drop column if exists updated_at,
  drop column if exists created_by,
  drop column if exists updated_by,
  drop column if exists version;

alter table step_ingredients
  drop constraint if exists step_ingredients_pkey;

alter table step_ingredients
  add primary key (step_id, recipe_ingredient_id);
