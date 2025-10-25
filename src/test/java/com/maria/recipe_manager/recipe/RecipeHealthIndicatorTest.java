package com.maria.recipe_manager.recipe;

import com.maria.recipe_manager.health.RecipeHealthIndicator;
import com.maria.recipe_manager.persistence.repo.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.Status;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RecipeHealthIndicatorTest {

    @Mock
    RecipeRepository recipeRepository;

    @InjectMocks
    RecipeHealthIndicator indicator;

    @Test
    void health_isUp_whenCountAtLeastOne() {
        when(recipeRepository.count()).thenReturn(1L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.UP);
        assertThat(health.getDetails())
                .containsEntry("count", 1L)
                .containsKey("durationMs");
        verify(recipeRepository, times(1)).count();
    }

    @Test
    void health_isOutOfService_whenCountIsZero() {
        when(recipeRepository.count()).thenReturn(0L);

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.OUT_OF_SERVICE);
        assertThat(health.getDetails())
                .containsEntry("reason", "no recipes")
                .containsEntry("count", 0L)
                .containsKey("durationMs");
        verify(recipeRepository, times(1)).count();
    }

    @Test
    void health_isDown_onRepositoryException() {
        when(recipeRepository.count()).thenThrow(new RuntimeException("db down"));

        Health health = indicator.health();

        assertThat(health.getStatus()).isEqualTo(Status.DOWN);
        assertThat(health.getDetails()).containsKey("durationMs");
        verify(recipeRepository, times(1)).count();
    }
}
