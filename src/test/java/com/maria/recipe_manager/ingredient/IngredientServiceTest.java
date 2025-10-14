package com.maria.recipe_manager.ingredient;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.persistence.IngredientDao;
import com.maria.recipe_manager.persistence.RecipeIngredientDao;
import com.maria.recipe_manager.service.IngredientService;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.ingredient.CreateIngredientRequest;
import com.maria.recipe_manager.web.ingredient.UpdateIngredientRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(MockitoExtension.class)
public class IngredientServiceTest {
    @Mock
    IngredientDao ingredientDao;

    @InjectMocks
    IngredientService service;

    @Autowired
    MockMvc mvc;

    @Test
    void create_ok() {
        // given
        var req = new CreateIngredientRequest();
        req.setName("milk");
        req.setUnit("ml");

        var saved = new Ingredient();
        saved.setId(10L);
        saved.setName("milk");
        saved.setUnit("ml");

        when(ingredientDao.save(any(Ingredient.class))).thenReturn(saved);

        // when
        var result = service.create(req);

        // then
        assertThat(result.getId()).isEqualTo(10L);
        assertThat(result.getName()).isEqualTo("milk");
        assertThat(result.getUnit()).isEqualTo("ml");
        verify(ingredientDao).save(any(Ingredient.class));
    }

    @Test
    void listAll_ok() {
        var ing = new Ingredient();
        ing.setId(2L); ing.setName("sugar"); ing.setUnit("g");
        when(ingredientDao.findAll()).thenReturn(List.of(ing));

        var list = service.listAll();

        assertThat(list).hasSize(1);
        assertThat(list.get(0).getName()).isEqualTo("sugar");
        verify(ingredientDao).findAll();
    }

    @Test
    void get_404() {
        when(ingredientDao.findById(999L)).thenReturn(null);

        assertThrows(NotFoundException.class, () -> service.get(999L));
        verify(ingredientDao).findById(999L);
    }

    @Test
    void update_ok() {
        var existing = new Ingredient();
        existing.setId(5L);
        existing.setName("old");
        existing.setUnit("u");

        when(ingredientDao.findById(5L)).thenReturn(existing);
        when(ingredientDao.existsOtherWithName(5L, "salt")).thenReturn(false);
        when(ingredientDao.merge(any(Ingredient.class)))
                .thenAnswer(inv -> inv.getArgument(0));

        var req = new UpdateIngredientRequest();
        req.setName("salt");
        req.setUnit("g");

        var updated = service.update(5L, req);

        assertThat(updated).isNotNull();
        assertThat(updated.getId()).isEqualTo(5L);
        assertThat(updated.getName()).isEqualTo("salt");
        assertThat(updated.getUnit()).isEqualTo("g");

        verify(ingredientDao).findById(5L);
        verify(ingredientDao).existsOtherWithName(5L, "salt");
        verify(ingredientDao).merge(any(Ingredient.class));
        verifyNoMoreInteractions(ingredientDao);
    }

    @Test
    void update_conflict_name_in_use() {
        var existing = new Ingredient();
        existing.setId(5L);
        existing.setName("old");
        existing.setUnit("u");

        when(ingredientDao.findById(5L)).thenReturn(existing);
        when(ingredientDao.existsOtherWithName(5L, "salt")).thenReturn(true);

        var req = new UpdateIngredientRequest();
        req.setName("salt");
        req.setUnit("g");

        assertThrows(IllegalArgumentException.class, () -> service.update(5L, req));

        verify(ingredientDao).findById(5L);
        verify(ingredientDao).existsOtherWithName(5L, "salt");
        verify(ingredientDao, never()).merge(any());
    }

    @Test
    void update_404_not_found() {
        when(ingredientDao.findById(123L)).thenReturn(null);

        var req = new UpdateIngredientRequest();
        req.setName("anything");
        req.setUnit("g");

        assertThrows(NotFoundException.class, () -> service.update(123L, req));

        verify(ingredientDao).findById(123L);
        verify(ingredientDao, never()).existsOtherWithName(anyLong(), anyString());
        verify(ingredientDao, never()).merge(any());
    }

}
