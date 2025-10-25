package com.maria.recipe_manager.web.recipe;

import com.maria.recipe_manager.messaging.RecipeEventPublisher;
import com.maria.recipe_manager.messaging.model.RecipeCreatedEvent;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.concurrent.ExecutionException;

//endpoint rest pt testare rabbitmq
@RestController
@RequestMapping("/internal/recipes")
public class RecipePublishController {
    private final RecipeEventPublisher publisher;

    public RecipePublishController(RecipeEventPublisher publisher) {
        this.publisher = publisher;
    }
    @PostMapping("/publish")
    public ResponseEntity<?> publish(@RequestBody RecipeCreatedEvent.RecipePayload payload)
            throws ExecutionException, InterruptedException {
        var event = RecipeCreatedEvent.of(payload, "recipe-manager");
        publisher.publishRecipeCreated(event).get(); // așteaptă confirmarea ACK
        return ResponseEntity.accepted().body(event);
    }

    // exemplu de payload pentru test
    @GetMapping("/sample")
    public RecipeCreatedEvent.RecipePayload sample() {
        return new RecipeCreatedEvent.RecipePayload(
                "ext-123",
                "Pasta Primavera",
                "EASY",
                25,
                "Boil pasta; saute veggies; mix.",
                List.of(
                        new RecipeCreatedEvent.IngredientItem("Pasta", 200, "g"),
                        new RecipeCreatedEvent.IngredientItem("Peas", 50, "g")
                )
        );
    }
}
