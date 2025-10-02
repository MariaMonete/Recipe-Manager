package com.maria.recipe_manager.service;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.persistence.IngredientDao;
import com.maria.recipe_manager.web.CreateIngredientRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientDao dao;

    public IngredientService (IngredientDao dao){
        this.dao=dao;
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
    public boolean deleteById(Long id){
        Ingredient i=dao.findById(id);
        if(i==null) return false;
        dao.delete(i);
        return true;
    }
}
