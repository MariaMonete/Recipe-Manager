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

    @PostMapping
    public ResponseEntity<Recipe> create(@Valid @RequestBody CreateRecipeRequest req){
        Recipe created= service.create(req);
        return ResponseEntity.created((URI.create("/api/recipes/"+created.getId()))).body(created);
    }

    @GetMapping
    public List<Recipe> list(){
        return service.listAll();
    }
}
