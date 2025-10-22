package com.maria.recipe_manager.service.scheduler;

import com.maria.recipe_manager.persistence.repo.IngredientRepository;
import io.micrometer.core.instrument.MeterRegistry;
import org.slf4j.LoggerFactory;
import org.slf4j.Logger;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class IngredientCountScheduler {

    private static final Logger log= LoggerFactory.getLogger(IngredientCountScheduler.class);
    private static final ZoneId ZONE=ZoneId.of("Europe/Bucharest");

    private final IngredientRepository ingredientRepository;
    private final AtomicLong ingredientCountGauge;

    public IngredientCountScheduler(IngredientRepository ingredientRepository, MeterRegistry meterRegistry) {
        this.ingredientRepository = ingredientRepository;
        // Gauge sustinut de un AtomicLong; Prometheus citeste valoarea curenta fara a interoga DB
        this.ingredientCountGauge = meterRegistry.gauge("recipe_manager_ingredient_count",
                new AtomicLong(0L));
    }

    // La fiecare ora, la fix (minute=0, secunde=0), in fusul Europe/Bucharest
    @Scheduled(cron = "0 * * * * *", zone = "Europe/Bucharest")
    public void logHourlyIngredientCount() {
        long t0 = System.nanoTime();
        long count = ingredientRepository.count(); // Spring Data JPA -> SELECT COUNT(*)
        ingredientCountGauge.set(count);
        long durationMs = (System.nanoTime() - t0) / 1_000_000;

        OffsetDateTime now = OffsetDateTime.now(ZONE);
        log.info("Ingredient count at {} = {}, durationMs={}", now, count, durationMs);
    }
}
