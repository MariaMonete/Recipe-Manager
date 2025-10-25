package com.maria.recipe_manager.health;

import com.maria.recipe_manager.persistence.repo.IngredientRepository;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.stereotype.Component;

@Component("Ingredient")
public class IngredientHealthIndicator implements HealthIndicator {

    private final IngredientRepository ingredientRepository;

    public IngredientHealthIndicator(IngredientRepository ingredientRepository) {
        this.ingredientRepository = ingredientRepository;
    }

    @Override
    public Health health(){
        long t0= System.nanoTime();
        try{
            long count=ingredientRepository.count();
            long durationMs=(System.nanoTime()-t0)/1_000_000;

            if(count>=1){
                return Health.up()
                        .withDetail("count",count)
                        .withDetail("durationMs",durationMs)
                        .build();
            }else{
                return Health.outOfService()
                        .withDetail("reason","no ingredients")
                        .withDetail("count",count)
                        .withDetail("durationMs",durationMs)
                        .build();
            }
        }catch (Exception ex){
            long durationMs=(System.nanoTime()-t0)/1_000_000;
            return Health.down(ex)
                    .withDetail("durationMs",durationMs)
                    .build();
        }
    }
}
