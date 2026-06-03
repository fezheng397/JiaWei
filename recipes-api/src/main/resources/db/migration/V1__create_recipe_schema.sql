create extension if not exists pgcrypto;

create table authors (
  id uuid primary key default gen_random_uuid(),
  public_id varchar(26) unique not null check (public_id ~ '^[0-9A-HJKMNP-TV-Z]{26}$'),
  name text not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  created_by uuid,
  updated_by uuid,
  version integer not null default 0
);

create table ingredients (
  id uuid primary key default gen_random_uuid(),
  public_id varchar(26) unique not null check (public_id ~ '^[0-9A-HJKMNP-TV-Z]{26}$'),
  name text not null,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  created_by uuid,
  updated_by uuid,
  version integer not null default 0
);

create table recipes (
  id uuid primary key default gen_random_uuid(),
  public_id varchar(26) unique not null check (public_id ~ '^[0-9A-HJKMNP-TV-Z]{26}$'),
  name text not null,
  author_id uuid not null references authors(id),
  description text not null,
  published_at timestamptz,
  hero_image_url text,
  prep_time_minutes integer,
  cook_time_minutes integer,
  difficulty text check (difficulty is null or difficulty in ('EASY', 'MEDIUM', 'HARD')),
  kind text not null check (kind in ('DISH', 'INGREDIENT')),
  ingredient_id uuid references ingredients(id),
  servings integer,
  yield_quantity text,
  yield_unit text,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  created_by uuid,
  updated_by uuid,
  version integer not null default 0,
  check (
    (kind = 'DISH' and ingredient_id is null and yield_quantity is null and yield_unit is null)
    or
    (kind = 'INGREDIENT' and ingredient_id is not null and servings is null and yield_quantity is not null)
  )
);

create table recipe_tags (
  recipe_id uuid not null references recipes(id) on delete cascade,
  position integer not null,
  tag text not null,
  primary key (recipe_id, position)
);

create table recipe_ingredients (
  id uuid primary key default gen_random_uuid(),
  public_id varchar(26) unique not null check (public_id ~ '^[0-9A-HJKMNP-TV-Z]{26}$'),
  recipe_id uuid not null references recipes(id) on delete cascade,
  ingredient_id uuid not null references ingredients(id),
  quantity text not null,
  unit text,
  position integer not null,
  prepared_by_recipe_id uuid references recipes(id),
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  created_by uuid,
  updated_by uuid,
  version integer not null default 0,
  unique (recipe_id, position)
);

create table recipe_steps (
  id uuid primary key default gen_random_uuid(),
  public_id varchar(26) unique not null check (public_id ~ '^[0-9A-HJKMNP-TV-Z]{26}$'),
  recipe_id uuid not null references recipes(id) on delete cascade,
  position integer not null,
  instructions text not null,
  timer_minutes integer,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  created_by uuid,
  updated_by uuid,
  version integer not null default 0,
  unique (recipe_id, position)
);

create table step_ingredients (
  id uuid primary key default gen_random_uuid(),
  public_id varchar(26) unique not null check (public_id ~ '^[0-9A-HJKMNP-TV-Z]{26}$'),
  step_id uuid not null references recipe_steps(id) on delete cascade,
  recipe_ingredient_id uuid not null references recipe_ingredients(id) on delete cascade,
  created_at timestamptz not null default now(),
  updated_at timestamptz not null default now(),
  created_by uuid,
  updated_by uuid,
  version integer not null default 0,
  unique (step_id, recipe_ingredient_id)
);

create index idx_authors_public_id on authors(public_id);
create index idx_ingredients_public_id on ingredients(public_id);
create index idx_recipes_public_id on recipes(public_id);
create index idx_recipes_kind on recipes(kind);
create index idx_recipes_ingredient_id on recipes(ingredient_id);
create index idx_recipe_ingredients_recipe_id on recipe_ingredients(recipe_id);
create index idx_recipe_ingredients_ingredient_id on recipe_ingredients(ingredient_id);
create index idx_recipe_steps_recipe_id on recipe_steps(recipe_id);
