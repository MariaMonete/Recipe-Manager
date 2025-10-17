package com.maria.recipe_manager.service;


import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.persistence.old.RecipeDao;
import com.maria.recipe_manager.persistence.repo.RecipeRepository;
import com.maria.recipe_manager.web.recipe.CreateRecipeRequest;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.recipe.PatchRecipeRequest;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RecipeService {
    private final RecipeRepository repo;

    public RecipeService(RecipeRepository repo) {
        this.repo = repo;
    }

    @Transactional
    public Recipe create(CreateRecipeRequest req){
        var r=new Recipe();
        r.setName(req.getName());
        r.setDifficulty(req.getDifficulty());
        r.setCookTimeMinutes(req.getCookTimeMinutes());
        r.setSteps(req.getSteps());

        r=repo.save(r); //face persist
        return repo.findById(r.getId()).orElse(r);
    }

    @Transactional(readOnly = true)
    public List<Recipe> listAll(){
        return repo.findAllOrdered();
    }

    //pt 404
    @Transactional(readOnly = true)
    public Recipe getById(Long id){
        return repo.findById(id).orElseThrow(()->new NotFoundException("Recipe",id));
    }

    //expunere operatie delete
    @Transactional
    public void delete(Long id){
        if (!repo.existsById(id)) {
            throw new NotFoundException("Recipe", id);
        }
        repo.deleteById(id);
    }
    //update PUT->vedem ca exista si se face validare pe campuri
    @Transactional
    public Recipe update(Long id, CreateRecipeRequest req){
        var r=repo.findById(id).orElseThrow(()->new NotFoundException("Recipe",id));
        r.setName(req.getName());
        r.setDifficulty(req.getDifficulty());
        r.setCookTimeMinutes(req.getCookTimeMinutes());
        r.setSteps(req.getSteps());
        return r;
    }

    //update PATCH
    @Transactional
    public Recipe patch(Long id, PatchRecipeRequest req){
        var r=repo.findById(id).orElseThrow(()->new NotFoundException("Recipe",id));
        if(r==null){
            throw new NotFoundException("Recipe",id);
        }
        //validari campuri not null
        if(req.getName()!=null){
            r.setName(req.getName().trim());
        }
        if(req.getDifficulty()!=null){
            r.setDifficulty(req.getDifficulty());
        }
        if(req.getCookTimeMinutes()!=null){
            r.setCookTimeMinutes(req.getCookTimeMinutes());
        }
        if(req.getSteps()!=null){
            r.setSteps(req.getSteps());
        }
        return r;
    }
    //folosim si native query
    @Transactional(readOnly = true)
    public List<Recipe> searchByNameNative(String name){
        return repo.searchByNameNative(name);
    }
}
