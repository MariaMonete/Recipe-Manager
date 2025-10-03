package com.maria.recipe_manager.web;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.service.IngredientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/ingredients")
public class IngredientController {
    private final IngredientService service;

    public IngredientController(IngredientService service){
        this.service=service;
    }

    @PostMapping(consumes="application/json", produces = "application/json")
    public ResponseEntity<Ingredient> create(@Valid @RequestBody CreateIngredientRequest req){
        Ingredient created= service.create(req);
        return ResponseEntity.created(URI.create("/api/ingredients/"+created.getId())).body(created);
    }

    @GetMapping(produces="application/json")
    public List<Ingredient> list(){
        return service.listAll();
    }

    @GetMapping(path="/{id}",produces="application/json")
    public Ingredient getOne(@PathVariable Long id){
        return service.get(id);
    }

    @PutMapping(path = "/{id}",consumes = "application/json", produces="application/json")
    public Ingredient update(@PathVariable Long id, @Valid @RequestBody UpdateIngredientRequest req){
        return service.update(id,req);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id){
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}
