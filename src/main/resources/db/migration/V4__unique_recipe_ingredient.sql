-- Unicitate pe (recipe_id, ingredient_id) ca să nu creezi dubluri în join table
ALTER TABLE recipe_ingredients
  ADD CONSTRAINT uq_recipe_ingredient UNIQUE (recipe_id, ingredient_id);

-- Indexuri utile pentru upsert rapid (dacă nu există deja)
CREATE INDEX IF NOT EXISTS ix_recipe_ingredients_recipe ON recipe_ingredients(recipe_id);
CREATE INDEX IF NOT EXISTS ix_recipe_ingredients_ingredient ON recipe_ingredients(ingredient_id);
