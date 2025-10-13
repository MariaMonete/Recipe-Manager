package com.maria.recipe_manager.recipe;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.maria.recipe_manager.model.Difficulty;
import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.service.RecipeService;
import com.maria.recipe_manager.web.exception.ApiExceptionHandler;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.recipe.CreateRecipeRequest;
import com.maria.recipe_manager.web.recipe.RecipeController;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = RecipeController.class)
@Import(ApiExceptionHandler.class)
public class RecipeControllerTest {

    @Autowired
    MockMvc mvc;

    @Autowired
    ObjectMapper om;

    @MockitoBean
    RecipeService service;

    @Test
    void list_ok() throws Exception {
        var r = new Recipe();
        r.setId(1L); r.setName("Pasta"); r.setDifficulty(Difficulty.EASY); r.setCookTimeMinutes(10);
        Mockito.when(service.listAll()).thenReturn(List.of(r));

        mvc.perform(get("/api/recipes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Pasta"));
    }

    @Test
    void getOne_404() throws Exception {
        Mockito.when(service.getById(999L))
                .thenThrow(new NotFoundException("Recipe", 999L));

        mvc.perform(get("/api/recipes/999"))
                .andExpect(status().isNotFound());
    }

    @Test
    void create_201() throws Exception {
        var req = new CreateRecipeRequest();
        req.setName("Soup"); req.setDifficulty(Difficulty.MEDIUM);
        req.setCookTimeMinutes(25); req.setSteps("...");

        var created = new Recipe();
        created.setId(42L); created.setName("Soup");
        Mockito.when(service.create(any())).thenReturn(created);

        mvc.perform(post("/api/recipes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(om.writeValueAsBytes(req)))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", "/api/recipes/42"))
                .andExpect(jsonPath("$.id").value(42));
    }
}
