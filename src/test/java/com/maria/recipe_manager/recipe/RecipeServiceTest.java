package com.maria.recipe_manager.recipe;

import com.maria.recipe_manager.model.Difficulty;
import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.persistence.old.RecipeDao;
import com.maria.recipe_manager.service.RecipeService;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.recipe.CreateRecipeRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeServiceTest {
    @Mock
    RecipeDao dao;
    @InjectMocks
    RecipeService service;

    @Test
    void getById_throws404_whenNull() {
        when(dao.findById(999L)).thenReturn(null);
        assertThrows(NotFoundException.class, () -> service.getById(999L));
    }

    @Test
    void delete_throws404_whenMissing() {
        when(dao.deleteById(999L)).thenReturn(false);
        assertThrows(NotFoundException.class, () -> service.delete(999L));
    }

    @Test
    void update_returnsUpdated_whenExists() {
        var r = new Recipe(); r.setId(1L);
        when(dao.findById(1L)).thenReturn(r);

        var req = new CreateRecipeRequest();
        req.setName("Noodles"); req.setDifficulty(Difficulty.EASY);
        req.setCookTimeMinutes(5); req.setSteps("x");

        var out = service.update(1L, req);
        assertEquals("Noodles", out.getName());
    }
}
