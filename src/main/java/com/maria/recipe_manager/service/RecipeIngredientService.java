package com.maria.recipe_manager.service;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.persistence.IngredientDao;
import com.maria.recipe_manager.persistence.RecipeDao;
import com.maria.recipe_manager.persistence.RecipeIngredientDao;
import com.maria.recipe_manager.web.AddIngredientToRecipeRequest;
import com.maria.recipe_manager.web.RecipeIngredientResponse;
import com.maria.recipe_manager.web.UpdateRecipeIngredientRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class RecipeIngredientService {
    private final RecipeDao recipeDao;
    private final IngredientDao ingredientDao;
    private final RecipeIngredientDao linkDao;

    public RecipeIngredientService(RecipeDao recipeDao,IngredientDao ingredientDao,RecipeIngredientDao linkDao){
        this.recipeDao=recipeDao;
        this.ingredientDao=ingredientDao;
        this.linkDao=linkDao;
    }

    @Transactional
    public RecipeIngredientResponse addOrUpdate(Long recipeId, AddIngredientToRecipeRequest req){
        Recipe recipe=recipeDao.findById(recipeId);
        if(recipe==null) throw new IllegalArgumentException("recipe not found");

        Ingredient ingredient=ingredientDao.findById(req.getIngredientId());
        if(ingredient==null) throw new IllegalArgumentException("ingredient not found");

        RecipeIngredient link=linkDao.findLink(recipeId,req.getIngredientId());
        if(link==null){
            link=new RecipeIngredient();
            link.setRecipe(recipe);
            link.setIngredient(ingredient);
        }
        link.setQuantity(req.getQuantity());

        if(link.getId()==null) linkDao.save(link);

        return new RecipeIngredientResponse(ingredient.getId(), ingredient.getName(), ingredient.getUnit(), link.getQuantity());
    }

    @Transactional(readOnly = true)
    public List<RecipeIngredientResponse> list(Long recipeId){
        return linkDao.listByRecipe(recipeId).stream()
                .map(ri->new RecipeIngredientResponse(
                        ri.getIngredient().getId(),
                        ri.getIngredient().getName(),
                        ri.getIngredient().getUnit(),
                        ri.getQuantity()
                )).toList();
    }

    @Transactional
    public boolean updateQuantity(Long recipeId, Long ingredientId, UpdateRecipeIngredientRequest req){
        RecipeIngredient link=linkDao.findLink(recipeId,ingredientId);
        if(link==null) return false;
        link.setQuantity(req.getQuantity());
        return true;
    }

    @Transactional
    public boolean remove(Long recipeId, Long ingredientId){
        RecipeIngredient link=linkDao.findLink(recipeId,ingredientId);
        if(link==null) return false;
        linkDao.delete(link);
        return true;
    }
}
