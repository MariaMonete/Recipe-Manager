package com.maria.recipe_manager.service.ingest;

import com.maria.recipe_manager.messaging.model.RecipeCreatedEvent;
import com.maria.recipe_manager.model.Difficulty;
import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.persistence.repo.IngredientRepository;
import com.maria.recipe_manager.persistence.repo.RecipeIngredientRepository;
import com.maria.recipe_manager.persistence.repo.RecipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
public class RecipeIngestService {
    private static final Logger log = LoggerFactory.getLogger(RecipeIngestService.class);

    private final RecipeRepository recipes;
    private final IngredientRepository ingredients;
    private final RecipeIngredientRepository recipeIngredients;

    public RecipeIngestService(RecipeRepository recipes,
                               IngredientRepository ingredients,
                               RecipeIngredientRepository recipeIngredients) {
        this.recipes = recipes;
        this.ingredients = ingredients;
        this.recipeIngredients = recipeIngredients;
    }

    @Transactional
    public void ingest(RecipeCreatedEvent e) {
        var p = e.recipe();
        if (p == null) {
            log.warn("RecipeCreatedEvent fără recipe, eventId={}", e.eventId());
            return;
        }

        // idempotenta: cautam dupa externalId; daca nu exista -> cream, upsert Recipe (idempotent pe externalId)
        var entity = recipes.findByExternalId(p.externalId())
                .orElseGet(() -> {
                    var r = new Recipe();
                    r.setExternalId(p.externalId());
                    return r;
                });

        // mapare campuri
        entity.setName(p.name());
        entity.setDifficulty(Difficulty.valueOf(p.difficulty()));
        entity.setCookTimeMinutes(p.cookTimeMinutes());
        entity.setSteps(p.steps());
        entity = recipes.save(entity); // asigură ID

        // 2) Upsert pentru Ingredient + legătura RecipeIngredient (idempotent pe pereche)
        // 2) upsert Ingredient + link
        if (p.ingredients() == null || p.ingredients().isEmpty()) {
            log.info("No ingredients for recipe extId={}", p.externalId());
            return;
        }

        for (var pi : p.ingredients()) {
            if (pi == null || pi.name() == null || pi.name().isBlank()) continue;

            var ing = ingredients.findByNameIgnoreCase(pi.name().trim())
                    .orElseGet(() -> {
                        var ni = new Ingredient();
                        ni.setName(pi.name().trim());
                        // unit e NOT NULL la tine -> trebuie setat la insert
                        ni.setUnit(pi.unit() != null ? pi.unit() : "unit"); // fallback safe
                        return ingredients.save(ni);
                    });

            // dacă unit vine diferit față de ce e în DB, poți decide să îl actualizezi
            if (pi.unit() != null && !pi.unit().equals(ing.getUnit())) {
                ing.setUnit(pi.unit());
                ingredients.save(ing);
            }

            final var finalEntity = entity;
            var link = recipeIngredients
                    .findByRecipeIdAndIngredientId(entity.getId(), ing.getId())
                    .orElseGet(() -> {
                        var ri = new RecipeIngredient();
                        ri.setRecipe(finalEntity);
                        ri.setIngredient(ing);
                        return ri;
                    });

            // quantity e BigDecimal în entitate; în DTO am pus tot BigDecimal
            link.setQuantity(pi.quantity() != null ?  pi.quantity() :  new BigDecimal("0.01"));
            recipeIngredients.save(link);
        }
    }
}
