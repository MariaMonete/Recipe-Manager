package com.maria.recipe_manager.service;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.persistence.old.IngredientDao;
import com.maria.recipe_manager.persistence.old.RecipeIngredientDao;
import com.maria.recipe_manager.persistence.repo.IngredientRepository;
import com.maria.recipe_manager.web.ingredient.CreateIngredientRequest;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.ingredient.UpdateIngredientRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class IngredientService {
    private final IngredientRepository repo;
    private final RecipeIngredientDao riRepo;

    public IngredientService(IngredientRepository repo, RecipeIngredientDao riRepo) {
        this.repo = repo;
        this.riRepo = riRepo;
    }

    @Transactional
    public Ingredient create(CreateIngredientRequest req){
        if (repo.existsByNameIgnoreCase(req.getName())) {
            throw new IllegalArgumentException("ingredient name already exists");
        }
        var i=new Ingredient();
        i.setName(req.getName());
        i.setUnit(req.getUnit());
        return repo.save(i);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> listAll(){
        return repo.findAllOrdered();
    }

    @Transactional
    public void delete(Long id) {
        // prevenim 500 din FK restrict -> dăm 409 Conflict prietenos
        if (riRepo.countUsageOfIngredients(id) > 0) {
            throw new IllegalStateException("Ingredient is used by at least one recipe");
        }
        // verificăm și existența
        if(!repo.existsById(id)){
            throw new NotFoundException("Ingredient",id);
        }
        repo.deleteById(id);
    }

    @Transactional(readOnly = true)
    public Ingredient get(Long id){
        return repo.findById(id).orElseThrow(()->new NotFoundException("Ingredient",id));
    }

    @Transactional
    public Ingredient update(Long id, UpdateIngredientRequest req) {
        var ing = get(id); // aruncă 404 dacă nu există
        if (repo.existsOtherWithName(id, req.getName())) {
            throw new IllegalArgumentException("Ingredient name already in use");
        }
        ing.setName(req.getName());
        ing.setUnit(req.getUnit());
        return repo.save(ing);
    }

    @Transactional(readOnly = true)
    public List<Ingredient> searchByNameNative(String name) {
        return repo.findAllNative(name);
    }
}
