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
