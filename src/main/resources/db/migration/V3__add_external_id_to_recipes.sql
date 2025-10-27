-- adauga coloana external_id si index unic
ALTER TABLE recipes
    ADD COLUMN IF NOT EXISTS external_id VARCHAR(255);

-- daca vrei sa fie obligatoriu dupa populare, seteaza NOT NULL;
-- initial las-o NULL-safe ca sa nu pice pe datele existente
CREATE UNIQUE INDEX IF NOT EXISTS ux_recipes_external_id
    ON recipes (external_id)
    WHERE external_id IS NOT NULL;