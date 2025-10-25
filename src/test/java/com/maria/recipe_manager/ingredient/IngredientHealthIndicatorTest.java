package com.maria.recipe_manager.ingredient;

import com.maria.recipe_manager.health.IngredientHealthIndicator;
import com.maria.recipe_manager.persistence.repo.IngredientRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class IngredientHealthIndicatorTest {

    @Mock
    IngredientRepository ingredientRepository;

    @InjectMocks
    IngredientHealthIndicator indicator;

    @Test
    void health_isUp_whenCountAtLeastOne() {
        when(ingredientRepository.count()).thenReturn(2L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails())
                .containsKey("count")
                .containsKey("durationMs");
        assertThat(health.getDetails().get("count")).isEqualTo(2L);
        verify(ingredientRepository, times(1)).count();
    }

    @Test
    void health_isOutOfService_whenCountIsZero() {
        when(ingredientRepository.count()).thenReturn(0L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.OUT_OF_SERVICE);
        assertThat(health.getDetails())
                .containsEntry("reason", "no ingredients")
                .containsEntry("count", 0L)
                .containsKey("durationMs");
        verify(ingredientRepository, times(1)).count();
    }

    @Test
    void health_isDown_onRepositoryException() {
        when(ingredientRepository.count()).thenThrow(new RuntimeException("db down"));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("durationMs");
        verify(ingredientRepository, times(1)).count();
    }
}
