alter table recipes
  add column hero_image_id uuid references media_assets(id);

create index idx_recipes_hero_image_id on recipes(hero_image_id);
