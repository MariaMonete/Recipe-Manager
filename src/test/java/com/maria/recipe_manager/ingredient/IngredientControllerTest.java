package com.maria.recipe_manager.ingredient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.service.IngredientService;
import com.maria.recipe_manager.web.exception.ApiExceptionHandler;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.ingredient.CreateIngredientRequest;
import com.maria.recipe_manager.web.ingredient.IngredientController;
import com.maria.recipe_manager.web.ingredient.UpdateIngredientRequest;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = IngredientController.class)
@Import(ApiExceptionHandler.class)
public class IngredientControllerTest {
    @Autowired
    MockMvc mvc;
    @Autowired
    ObjectMapper om;
    @MockitoBean
    IngredientService service;

    @Test
    void list_ok() throws Exception {
        var ing = new Ingredient();
        ing.setId(2L); ing.setName("Salt"); ing.setUnit("g");
        Mockito.when(service.listAll()).thenReturn(List.of(ing));

        mvc.perform(get("/api/ingredients"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Salt"));
    }

    @Test
    void getOne_404() throws Exception {
        Mockito.when(service.get(999L))
                .thenThrow(new NotFoundException("Ingredient", 999L));

        mvc.perform(get("/api/ingredients/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_201() throws Exception {
        var req = new CreateIngredientRequest();
        req.setName("Flour"); req.setUnit("g");

        var created = new Ingredient();
        created.setId(5L); created.setName("Flour"); created.setUnit("g");
        Mockito.when(service.create(any())).thenReturn(created);

        mvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/ingredients/5"))
                .andExpect(jsonPath("$.id").value(5));
    }

    @Test
    void create_400_whenNameBlank() throws Exception {
        var req = new CreateIngredientRequest();
        req.setName("   ");           // invalid (@NotBlank)
        req.setUnit("ml");

        mvc.perform(post("/api/ingredients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void update_put_200() throws Exception {
        var req = new UpdateIngredientRequest();
        req.setName("salt"); req.setUnit("g");

        var updated = new Ingredient();
        updated.setId(5L); updated.setName("salt"); updated.setUnit("g");
        Mockito.when(service.update(eq(5L), any())).thenReturn(updated);

        mvc.perform(put("/api/ingredients/5")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(req)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5))
                .andExpect(jsonPath("$.name").value("salt"));
    }

    @Test
    void delete_204() throws Exception {
        mvc.perform(delete("/api/ingredients/5"))
                .andExpect(status().isNoContent());
        Mockito.verify(service).delete(5L);
    }

}
