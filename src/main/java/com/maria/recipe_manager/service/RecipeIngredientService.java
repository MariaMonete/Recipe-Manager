package com.maria.recipe_manager.service;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.persistence.old.RecipeIngredientDao;
import com.maria.recipe_manager.persistence.repo.IngredientRepository;
import com.maria.recipe_manager.persistence.repo.RecipeIngredientRepository;
import com.maria.recipe_manager.persistence.repo.RecipeRepository;
import com.maria.recipe_manager.web.recipeingredient.AddIngredientToRecipeRequest;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.recipeingredient.RecipeIngredientResponse;
import com.maria.recipe_manager.web.recipeingredient.UpdateRecipeIngredientRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeIngredientService {
    private final RecipeIngredientRepository riRepo;
    private final RecipeRepository recipeRepo;
    private final IngredientRepository ingredientRepo;

    public RecipeIngredientService(RecipeIngredientRepository riRepo, RecipeRepository recipeRepo, IngredientRepository ingredientRepo) {
        this.riRepo = riRepo;
        this.recipeRepo = recipeRepo;
        this.ingredientRepo = ingredientRepo;
    }

    private Recipe mustFindRecipe(Long id){
        return recipeRepo.findById(id).orElseThrow(()->new NotFoundException("Recipe",id));
    }

    private Ingredient mustFindIngredient(Long id){
        return ingredientRepo.findById(id).orElseThrow(()->new NotFoundException("Ingredient",id));
    }

    @Transactional
    public RecipeIngredient add(Long recipeId, AddIngredientToRecipeRequest req) {
        var recipe = mustFindRecipe(recipeId);
        var ingredient = mustFindIngredient(req.getIngredientId());

        if (riRepo.existsByRecipeAndIngredient(recipeId, req.getIngredientId())) {
            throw new IllegalStateException("Ingredient already added to recipe");
        }

        var ri = new RecipeIngredient();
        ri.setRecipe(recipe);
        ri.setIngredient(ingredient);
        ri.setQuantity(req.getQuantity());
        return riRepo.save(ri);
    }

    @Transactional(readOnly = true)
    public RecipeIngredientResponse getOneByRiId(Long recipeId, Long riId) {
        var view = riRepo.findOneRowByRiIdAndRecipeId(recipeId, riId)
                .orElseThrow(() ->
                        new NotFoundException("RecipeIngredient " ,riId));

        // row = { ri.id, ing.id, ing.name, ing.unit, ri.quantity }
        return new RecipeIngredientResponse(
                view.getId(),
                view.getIngredientId(),
                view.getIngredientName(),
                view.getUnit(),
                view.getQuantity()
        );
    }


    @Transactional(readOnly = true)
    public List<RecipeIngredientResponse> list(Long recipeId) {
        return riRepo.findAllRowsForRecipe(recipeId).stream()
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
        var ri = riRepo.findById(riId).orElse(null);
        if (ri == null || !ri.getRecipe().getId().equals(recipeId)) {
            throw new IllegalArgumentException("RecipeIngredient not found for this recipe");
        }
        riRepo.updateQuantity(riId, req.getQuantity());
    }

    @Transactional
    public void delete(Long recipeId, Long riId) {
        var ri = riRepo.findById(riId).orElse(null);
        if (ri == null || !ri.getRecipe().getId().equals(recipeId)) {
            throw new IllegalArgumentException("RecipeIngredient not found for this recipe");
        }
        riRepo.deleteById(riId);
    }
}
