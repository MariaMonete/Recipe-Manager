-- 1) Tabela recipes
create table if not exists recipes (
  id bigserial primary key,
  name varchar(255) not null,
  difficulty varchar(16) not null,
  cook_time_minutes int not null,
  steps text,
  created_at timestamp not null default now(),
  constraint chk_recipes_difficulty
    check (difficulty in ('EASY','MEDIUM','HARD'))
);

create index if not exists idx_recipes_name on recipes(name);

-- 2) Tabela ingredients
create table if not exists ingredients (
  id bigserial primary key,
  name varchar(128) not null unique,
  unit varchar(16) not null  -- ex: g, ml, pcs
);

-- 3) Tabela de legatura recipe_ingredients (many-to-many cu payload: quantity)
create table if not exists recipe_ingredients (
  id bigserial primary key,
  recipe_id bigint not null references recipes(id) on delete cascade,
  ingredient_id bigint not null references ingredients(id) on delete restrict,
  quantity numeric(10,2) not null
);

create index if not exists idx_ri_recipe on recipe_ingredients(recipe_id);
create index if not exists idx_ri_ingredient on recipe_ingredients(ingredient_id);

-- 4) Date initiale minimale (seed) pentru ingrediente
insert into ingredients(name, unit) values
  ('flour','g'),
  ('milk','ml'),
  ('egg','pcs')
on conflict (name) do nothing;