package com.maria.recipe_manager.web;

import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.service.RecipeIngredientService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
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

    @GetMapping(path = "/{riId}", produces = "application/json")
    public RecipeIngredientResponse getOneByRiId(
            @PathVariable("recipeId") Long recipeId,
            @PathVariable("riId") Long riId) {
        return service.getOneByRiId(recipeId, riId);
    }

    //endpoint POST
    @PostMapping(consumes = "application/json", produces="application/json")
    public ResponseEntity<RecipeIngredient> addOrUpdate(@PathVariable Long recipeId, @Valid @RequestBody AddIngredientToRecipeRequest req){
        var created=service.add(recipeId,req);
        return ResponseEntity
                .created(URI.create("/api/recipes/" + recipeId + "/ingredients/" + created.getId()))
                .body(created);
    }

    //endpoint PATCH
    @PatchMapping(path="/{riId}",consumes="application/json")
    public ResponseEntity<Void> updateQuantity(@PathVariable Long recipeId, @PathVariable Long riId, @Valid @RequestBody UpdateRecipeIngredientRequest req) {
        service.updateQuantity(recipeId, riId, req);
        return ResponseEntity.noContent().build();
    }

    //endpoint DELETE
    @DeleteMapping("/{riId}")
    public ResponseEntity<Void> delete(@PathVariable Long recipeId, @PathVariable Long riId) {
        service.delete(recipeId, riId);
        return ResponseEntity.noContent().build();
    }
}
