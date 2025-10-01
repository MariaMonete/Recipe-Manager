package com.maria.recipe_manager.web;

//controller rest

import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.service.RecipeService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {
    private final RecipeService service;

    public RecipeController(RecipeService service){
        this.service=service;
    }

    @PostMapping(consumes = "application/json", produces = "application/json")
    public ResponseEntity<Recipe> create(@Valid @RequestBody CreateRecipeRequest req){
        Recipe created= service.create(req);
        return ResponseEntity.created((URI.create("/api/recipes/"+created.getId()))).body(created);
    }

    @GetMapping(produces = "application/json")
    public List<Recipe> list(){
        return service.listAll();
    }

    //endpoint pt un singur recipe
    @GetMapping(value="/{id}", produces="application/json")
    public Recipe getOne(@PathVariable Long id){ //leaga {id} din url de param metodei
        return service.getById(id);
    }

    //endpoint pt delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

    //endpoint update- cu put
    @PutMapping(value="/{id}",consumes="application/json",produces = "application/json")
    public ResponseEntity<Recipe> update(@PathVariable Long id, @Valid @RequestBody CreateRecipeRequest req){
        Recipe updated=service.update(id,req);
        return ResponseEntity.ok(updated);
    }

    //endpoint update - cu patch
    @PatchMapping(value = "/{id}",consumes="application/json",produces="application/json")
    public ResponseEntity<Recipe> patch(@PathVariable Long id, @Valid @RequestBody PatchRecipeRequest req){
        Recipe updated= service.patch(id,req);
        return ResponseEntity.ok(updated);
    }
}
