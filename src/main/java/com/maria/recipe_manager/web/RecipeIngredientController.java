package com.maria.recipe_manager.web;

import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.service.RecipeIngredientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes/{recipeId}/ingredients")
public class RecipeIngredientController {
    private final RecipeIngredientService service;

    public RecipeIngredientController(RecipeIngredientService service){this.service=service;}

    //endpoint GET
    @GetMapping(produces = "application/json")
    public List<RecipeIngredientResponse> list(@PathVariable Long recipeId){
        return service.list(recipeId);
    }

    //endpoint POST
    @PostMapping(consumes = "application/json", produces="application/json")
    public ResponseEntity<RecipeIngredientResponse> addOrUpdate(@PathVariable Long recipeId, @Valid @RequestBody AddIngredientToRecipeRequest req){
        var response=service.addOrUpdate(recipeId,req);
        return ResponseEntity.ok(response);
    }

    //endpoint PATCH
    @PatchMapping(path="/{ingredientId}",consumes="application/json")
    public ResponseEntity<Void> updateQty(@PathVariable Long recipeId, @PathVariable Long ingredientId, @Valid @RequestBody UpdateRecipeIngredientRequest req){
        boolean ok=service.updateQuantity(recipeId,ingredientId,req);
        return ok ? ResponseEntity.noContent().build():ResponseEntity.notFound().build();
    }

    //endpoint DELETE
    @DeleteMapping("/{ingredientId}")
    public ResponseEntity<Void> remove(@PathVariable Long recipeId, @PathVariable Long ingredientId){
        boolean ok= service.remove(recipeId,ingredientId);
        return ok ? ResponseEntity.noContent().build():ResponseEntity.notFound().build();
    }
}
