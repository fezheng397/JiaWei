-- Constraint values must match RecipeDifficulty and RecipeKind enums.
comment on constraint recipes_difficulty_check on recipes is 'Must match RecipeDifficulty enum values.';
comment on constraint recipes_kind_check on recipes is 'Must match RecipeKind enum values.';

insert into step_ingredients (id, public_id, step_id, recipe_ingredient_id)
select
  '00000000-0000-4000-8000-000000006020',
  '01JY8CB6W8QYVDAV4RGE8K6R22',
  '00000000-0000-4000-8000-000000005013',
  '00000000-0000-4000-8000-000000004007'
where exists (
  select 1 from recipe_steps where id = '00000000-0000-4000-8000-000000005013'
) and exists (
  select 1 from recipe_ingredients where id = '00000000-0000-4000-8000-000000004007'
)
on conflict (step_id, recipe_ingredient_id) do nothing;

insert into step_ingredients (id, public_id, step_id, recipe_ingredient_id)
select
  '00000000-0000-4000-8000-000000006021',
  '01JY8CEKXQ4E66SRVD53TXFH1W',
  '00000000-0000-4000-8000-000000005013',
  '00000000-0000-4000-8000-000000004008'
where exists (
  select 1 from recipe_steps where id = '00000000-0000-4000-8000-000000005013'
) and exists (
  select 1 from recipe_ingredients where id = '00000000-0000-4000-8000-000000004008'
)
on conflict (step_id, recipe_ingredient_id) do nothing;

insert into step_ingredients (id, public_id, step_id, recipe_ingredient_id)
select
  '00000000-0000-4000-8000-000000006022',
  '01JY8CGCKPC6D88WZHMBHEJDPW',
  '00000000-0000-4000-8000-000000005013',
  '00000000-0000-4000-8000-000000004009'
where exists (
  select 1 from recipe_steps where id = '00000000-0000-4000-8000-000000005013'
) and exists (
  select 1 from recipe_ingredients where id = '00000000-0000-4000-8000-000000004009'
)
on conflict (step_id, recipe_ingredient_id) do nothing;

insert into step_ingredients (id, public_id, step_id, recipe_ingredient_id)
select
  '00000000-0000-4000-8000-000000006023',
  '01JY8CJY96N27XQPYAFJCM2MMH',
  '00000000-0000-4000-8000-000000005013',
  '00000000-0000-4000-8000-000000004010'
where exists (
  select 1 from recipe_steps where id = '00000000-0000-4000-8000-000000005013'
) and exists (
  select 1 from recipe_ingredients where id = '00000000-0000-4000-8000-000000004010'
)
on conflict (step_id, recipe_ingredient_id) do nothing;

insert into step_ingredients (id, public_id, step_id, recipe_ingredient_id)
select
  '00000000-0000-4000-8000-000000006024',
  '01JY8CMHKTRACSQZ12E8SE65KF',
  '00000000-0000-4000-8000-000000005014',
  '00000000-0000-4000-8000-000000004007'
where exists (
  select 1 from recipe_steps where id = '00000000-0000-4000-8000-000000005014'
) and exists (
  select 1 from recipe_ingredients where id = '00000000-0000-4000-8000-000000004007'
)
on conflict (step_id, recipe_ingredient_id) do nothing;

insert into step_ingredients (id, public_id, step_id, recipe_ingredient_id)
select
  '00000000-0000-4000-8000-000000006025',
  '01JY8CPABDVV6A5SQHT2W1DDPF',
  '00000000-0000-4000-8000-000000005014',
  '00000000-0000-4000-8000-000000004008'
where exists (
  select 1 from recipe_steps where id = '00000000-0000-4000-8000-000000005014'
) and exists (
  select 1 from recipe_ingredients where id = '00000000-0000-4000-8000-000000004008'
)
on conflict (step_id, recipe_ingredient_id) do nothing;

insert into step_ingredients (id, public_id, step_id, recipe_ingredient_id)
select
  '00000000-0000-4000-8000-000000006026',
  '01JY8CRACPKPWHBQ2YCM31B6RE',
  '00000000-0000-4000-8000-000000005014',
  '00000000-0000-4000-8000-000000004014'
where exists (
  select 1 from recipe_steps where id = '00000000-0000-4000-8000-000000005014'
) and exists (
  select 1 from recipe_ingredients where id = '00000000-0000-4000-8000-000000004014'
)
on conflict (step_id, recipe_ingredient_id) do nothing;

alter table recipe_tags drop constraint if exists recipe_tags_pkey;
alter table recipe_tags drop constraint if exists recipe_tags_public_id_key;
alter table recipe_tags drop constraint if exists recipe_tags_recipe_id_position_key;
alter table recipe_tags drop column if exists id;
alter table recipe_tags drop column if exists public_id;
alter table recipe_tags drop column if exists created_at;
alter table recipe_tags drop column if exists updated_at;
alter table recipe_tags drop column if exists created_by;
alter table recipe_tags drop column if exists updated_by;
alter table recipe_tags drop column if exists version;
alter table recipe_tags add primary key (recipe_id, position);
create index if not exists idx_recipe_tags_recipe_id on recipe_tags(recipe_id);

alter table step_ingredients drop constraint if exists step_ingredients_pkey;
alter table step_ingredients drop constraint if exists step_ingredients_public_id_key;
alter table step_ingredients drop constraint if exists step_ingredients_step_id_recipe_ingredient_id_key;
alter table step_ingredients drop column if exists id;
alter table step_ingredients drop column if exists public_id;
alter table step_ingredients drop column if exists created_at;
alter table step_ingredients drop column if exists updated_at;
alter table step_ingredients drop column if exists created_by;
alter table step_ingredients drop column if exists updated_by;
alter table step_ingredients drop column if exists version;
alter table step_ingredients add primary key (step_id, recipe_ingredient_id);
