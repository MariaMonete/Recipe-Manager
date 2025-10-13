package com.maria.recipe_manager.service;

import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.persistence.RecipeIngredientDao;
import com.maria.recipe_manager.web.recipeingredient.AddIngredientToRecipeRequest;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.recipeingredient.RecipeIngredientResponse;
import com.maria.recipe_manager.web.recipeingredient.UpdateRecipeIngredientRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeIngredientService {
    private final RecipeIngredientDao dao;

    public RecipeIngredientService(RecipeIngredientDao linkDao){
        this.dao=linkDao;
    }

    @Transactional
    public RecipeIngredient add(Long recipeId, AddIngredientToRecipeRequest req) {
        var recipe = dao.mustFindRecipe(recipeId);
        var ingredient = dao.mustFindIngredient(req.getIngredientId());

        if (dao.existsByRecipeAndIngredient(recipeId, req.getIngredientId())) {
            throw new IllegalStateException("Ingredient already added to recipe");
        }

        var ri = new RecipeIngredient();
        ri.setRecipe(recipe);
        ri.setIngredient(ingredient);
        ri.setQuantity(req.getQuantity());
        return dao.save(ri);
    }

    @Transactional(readOnly = true)
    public RecipeIngredientResponse getOneByRiId(Long recipeId, Long riId) {
        var row = dao.findOneRowByRiIdAndRecipeId(recipeId, riId)
                .orElseThrow(() ->
                        new NotFoundException("RecipeIngredient " ,riId));

        // row = { ri.id, ing.id, ing.name, ing.unit, ri.quantity }
        return new RecipeIngredientResponse(
                ((Number) row[0]).longValue(),
                ((Number) row[1]).longValue(),
                (String) row[2],
                (String) row[3],
                (java.math.BigDecimal) row[4]
        );
    }


    @Transactional(readOnly = true)
    public List<RecipeIngredientResponse> list(Long recipeId) {
        return dao.findAllRowsForRecipe(recipeId).stream()
                .map(r -> new RecipeIngredientResponse(
                        (Long) r[0], // ri.id
                        (Long) r[1], // ingredientId
                        (String) r[2], // ingredientName
                        (String) r[3], // unit
                        (java.math.BigDecimal) r[4] // quantity
                ))
                .toList();
    }

    @Transactional
    public void updateQuantity(Long recipeId, Long riId, UpdateRecipeIngredientRequest req) {
        var ri = dao.findById(riId);
        if (ri == null || !ri.getRecipe().getId().equals(recipeId)) {
            throw new IllegalArgumentException("RecipeIngredient not found for this recipe");
        }
        dao.updateQuantity(riId, req.getQuantity());
    }

    @Transactional
    public void delete(Long recipeId, Long riId) {
        var ri = dao.findById(riId);
        if (ri == null || !ri.getRecipe().getId().equals(recipeId)) {
            throw new IllegalArgumentException("RecipeIngredient not found for this recipe");
        }
        dao.deleteById(riId);
    }
}
