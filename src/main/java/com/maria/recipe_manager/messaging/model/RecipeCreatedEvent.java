package com.maria.recipe_manager.messaging.model;

import java.time.OffsetDateTime;
import java.util.List;
import java.util.UUID;

public record RecipeCreatedEvent(
        int schemaVersion,
        String eventId,
        String occurredAt,
        RecipePayload recipe,
        String source
) {
    public static RecipeCreatedEvent of(RecipePayload recipe, String source) {
        return new RecipeCreatedEvent(
                1,
                UUID.randomUUID().toString(),
                OffsetDateTime.now().toString(),
                recipe,
                source
        );
    }

    public record RecipePayload(
            String externalId,
            String name,
            String difficulty,
            Integer cookTimeMinutes,
            String steps,
            List<IngredientItem> ingredients
    ) {}

    public record IngredientItem(
            String name,
            Number quantity,
            String unit
    ) {}
}

