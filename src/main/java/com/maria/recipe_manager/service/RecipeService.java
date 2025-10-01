package com.maria.recipe_manager.service;


import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.persistence.RecipeDao;
import com.maria.recipe_manager.web.CreateRecipeRequest;
import com.maria.recipe_manager.web.NotFoundException;
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

    @Transactional(readOnly = true)
    public List<Recipe> listAll(){
        return recipeDao.findAll();
    }

    //pt 404
    @Transactional(readOnly = true)
    public Recipe getById(Long id){
        Recipe r=recipeDao.findById(id);
        if(r==null){
            throw new NotFoundException("Recipe",id);
        }
        return r;
    }

    //expunere operatie delete
    @Transactional
    public void delete(Long id){
        boolean removed=recipeDao.deleteById(id);
        if(!removed){
            //daca nu exista, intoarcem 404
            throw new NotFoundException("Recipe",id);
        }
    }
    //update->vedem ca exista si se face validare pe campuri
    @Transactional
    public Recipe update(Long id, CreateRecipeRequest req){
        Recipe r=recipeDao.findById(id);
        if(r==null){
            throw new NotFoundException("Recipe",id);
        }
        //setam campurile
        r.setName(req.getName());
        r.setDifficulty(req.getDifficulty());
        r.setCookTimeMinutes(req.getCookTimeMinutes());
        r.setSteps(req.getSteps());
        return r;
    }
}
