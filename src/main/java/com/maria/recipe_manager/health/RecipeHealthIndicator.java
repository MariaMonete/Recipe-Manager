package com.maria.recipe_manager.health;

import com.maria.recipe_manager.persistence.repo.RecipeRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("recipe")
public class RecipeHealthIndicator implements HealthIndicator {

    private final RecipeRepository recipeRepository;

    public RecipeHealthIndicator(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    @Override
    public Health health() {
        long t0 = System.nanoTime();
        try {
            long count = recipeRepository.count();
            long durationMs = (System.nanoTime() - t0) / 1_000_000;

            if (count >= 1) {
                return Health.up()
                        .withDetail("count", count)
                        .withDetail("durationMs", durationMs)
                        .build();
            } else {
                return Health.outOfService()
                        .withDetail("reason", "no recipes")
                        .withDetail("count", count)
                        .withDetail("durationMs", durationMs)
                        .build();
            }
        } catch (Exception ex) {
            long durationMs = (System.nanoTime() - t0) / 1_000_000;
            return Health.down(ex)
                    .withDetail("durationMs", durationMs)
                    .build();
        }
    }
}
