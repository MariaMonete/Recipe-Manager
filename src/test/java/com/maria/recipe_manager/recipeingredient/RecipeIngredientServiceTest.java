package com.maria.recipe_manager.recipeingredient;

import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.persistence.old.RecipeIngredientDao;
import com.maria.recipe_manager.service.RecipeIngredientService;
import com.maria.recipe_manager.web.recipeingredient.UpdateRecipeIngredientRequest;
import com.maria.recipe_manager.web.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecipeIngredientServiceTest {

    @Mock
    RecipeIngredientDao dao;
    @InjectMocks
    RecipeIngredientService service;

    @Test
    void getOneByRiId_returnsFTO(){
        Object[] row = new Object[]{10L, 2L, "Salt", "g", new BigDecimal("5.0")};
        when(dao.findOneRowByRiIdAndRecipeId(3L, 10L)).thenReturn(Optional.of(row));
        var dto = service.getOneByRiId(3L, 10L);
        assertEquals(10L, dto.getId());
        assertEquals(2L, dto.getIngredientId());
    }

    @Test
    void getOneByRiId_throws404_whenMissing() {
        when(dao.findOneRowByRiIdAndRecipeId(3L, 999L)).thenReturn(Optional.empty());
        assertThrows(NotFoundException.class, () -> service.getOneByRiId(3L, 999L));
    }

    @Test
    void updateQuantity_callsDao_whenOwnershipOk() {
        var ri = new RecipeIngredient();
        var recipe = new Recipe(); recipe.setId(3L);
        ri.setRecipe(recipe);
        when(dao.findById(10L)).thenReturn(ri);

        var req = new UpdateRecipeIngredientRequest();
        req.setQuantity(new BigDecimal("7.5"));

        service.updateQuantity(3L, 10L, req);

        verify(dao).updateQuantity(10L, new BigDecimal("7.5"));
    }

    @Test
    void updateQuantity_throws_whenOwnershipMismatch() {
        var ri = new RecipeIngredient();
        var recipe = new Recipe(); recipe.setId(99L);
        ri.setRecipe(recipe);
        when(dao.findById(10L)).thenReturn(ri);

        var req = new UpdateRecipeIngredientRequest();
        req.setQuantity(new BigDecimal("1"));

        assertThrows(IllegalArgumentException.class,
                () -> service.updateQuantity(3L, 10L, req));
    }
}
