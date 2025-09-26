package com.maria.recipe_manager.service;


import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.persistence.RecipeDao;
import com.maria.recipe_manager.web.CreateRecipeRequest;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeDao recipeDao;

    public RecipeService(RecipeDao recipeDao){
        this.recipeDao=recipeDao;
    }

    @Transactional
    public Recipe create(CreateRecipeRequest req){
        Recipe r=new Recipe();
        r.setName(req.getName());
        r.setDifficulty(req.getDifficulty());
        r.setCookTimeMinutes(req.getCookTimeMinutes());
        r.setSteps(req.getSteps());
        return recipeDao.save(r);
    }

    @Transactional
    public List<Recipe> listAll(){
        return recipeDao.findAll();
    }
}
