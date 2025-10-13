package com.maria.recipe_manager.recipeingredient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maria.recipe_manager.service.RecipeIngredientService;
import com.maria.recipe_manager.web.exception.ApiExceptionHandler;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.recipeingredient.RecipeIngredientController;
import com.maria.recipe_manager.web.recipeingredient.RecipeIngredientResponse;
import com.maria.recipe_manager.web.recipeingredient.UpdateRecipeIngredientRequest;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.math.BigDecimal;
import java.util.List;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RecipeIngredientController.class)
@Import(ApiExceptionHandler.class)
public class RecipeIngredientControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @MockitoBean
    RecipeIngredientService service;

    @Test
    void list_byRecipe_ok() throws Exception {
        var item = new RecipeIngredientResponse(10L, 2L, "Salt", "g", new BigDecimal("5.0"));
        Mockito.when(service.list(3L)).thenReturn(List.of(item));

        mvc.perform(get("/api/recipes/3/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(10))
                .andExpect(jsonPath("$[0].ingredientName").value("Salt"));
    }

    @Test
    void getOneByRiId_ok() throws Exception {
        var item = new RecipeIngredientResponse(10L, 2L, "Salt", "g", new BigDecimal("5.0"));
        Mockito.when(service.getOneByRiId(3L, 10L)).thenReturn(item);

        mvc.perform(get("/api/recipes/3/ingredients/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ingredientId").value(2));
    }

    @Test
    void getOneByRiId_404() throws Exception {
        Mockito.when(service.getOneByRiId(3L, 999L))
                .thenThrow(new NotFoundException("RecipeIngredient",999L));

        mvc.perform(get("/api/recipes/3/ingredients/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void patchQuantity_204() throws Exception {
        var req = new UpdateRecipeIngredientRequest();
        req.setQuantity(new BigDecimal("7.25"));

        mvc.perform(patch("/api/recipes/3/ingredients/10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(req)))
                .andExpect(status().isNoContent());

        var captor = ArgumentCaptor.forClass(UpdateRecipeIngredientRequest.class);
        Mockito.verify(service).updateQuantity(Mockito.eq(3L), Mockito.eq(10L), captor.capture());

        // Assert pe conținut, nu pe identitate
        assertEquals(0, captor.getValue().getQuantity()
                .compareTo(new BigDecimal("7.25")));

    }

}
