--unicitate ingredient in reteta
do $$
begin
  if not exists (
    select 1
    from pg_indexes
    where schemaname = 'public'
      and indexname = 'uk_recipe_ingredient_unique'
  ) then
    create unique index uk_recipe_ingredient_unique
      on recipe_ingredients(recipe_id, ingredient_id);
  end if;
end$$;

do $$
begin
  if not exists (
    select 1
    from information_schema.table_constraints
    where table_schema = 'public'
      and table_name   = 'recipe_ingredients'
      and constraint_type = 'CHECK'
  ) then
    alter table recipe_ingredients
      add constraint chk_recipe_ingredients_quantity
      check (quantity > 0);
  end if;
end$$;