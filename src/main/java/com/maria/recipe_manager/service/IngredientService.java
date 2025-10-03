package com.maria.recipe_manager.service;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.persistence.IngredientDao;
import com.maria.recipe_manager.persistence.RecipeIngredientDao;
import com.maria.recipe_manager.web.CreateIngredientRequest;
import com.maria.recipe_manager.web.NotFoundException;
import com.maria.recipe_manager.web.UpdateIngredientRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientDao dao;
    private final RecipeIngredientDao recipeIngredientDao;

    public IngredientService (IngredientDao dao,RecipeIngredientDao recipeIngredientDao){
        this.dao=dao;
        this.recipeIngredientDao=recipeIngredientDao;
    }

    @Transactional
    public Ingredient create(CreateIngredientRequest req){
        if (dao.existsByNameIgnoreCase(req.getName())) {
            throw new IllegalArgumentException("ingredient name already exists");
        }
        Ingredient i=new Ingredient();
        i.setName(req.getName());
        i.setUnit(req.getUnit());
        return dao.save(i);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> listAll(){
        return dao.findAll();
    }

    @Transactional
    public void delete(Long id) {
        // prevenim 500 din FK restrict -> dăm 409 Conflict prietenos
        if (recipeIngredientDao.countUsageOfIngredients(id) > 0) {
            throw new IllegalStateException("Ingredient is used by at least one recipe");
        }
        // verificăm și existența
        var ing =dao.findById(id);
        if (ing == null) throw new NotFoundException("Ingredient" ,id);
        dao.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Ingredient get(Long id){
        var ing=dao.findById(id);
        if(ing==null) throw new NotFoundException("Ingredient",id);
        return ing;
    }

    @Transactional
    public Ingredient update(Long id, UpdateIngredientRequest req) {
        var ing = get(id); // aruncă 404 dacă nu există
        if (dao.existsOtherWithName(id, req.getName())) {
            throw new IllegalArgumentException("Ingredient name already in use");
        }
        ing.setName(req.getName());
        ing.setUnit(req.getUnit());
        return dao.merge(ing);
    }
}
