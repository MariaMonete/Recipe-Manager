package com.maria.recipe_manager.recipeingredient;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.persistence.old.RecipeIngredientDao;
import com.maria.recipe_manager.persistence.repo.IngredientRepository;
import com.maria.recipe_manager.persistence.repo.RecipeIngredientRepository;
import com.maria.recipe_manager.persistence.repo.RecipeRepository;
import com.maria.recipe_manager.persistence.repo.helper.RecipeIngredientRowView;
import com.maria.recipe_manager.service.RecipeIngredientService;
import com.maria.recipe_manager.web.recipeingredient.AddIngredientToRecipeRequest;
import com.maria.recipe_manager.web.recipeingredient.RecipeIngredientResponse;
import com.maria.recipe_manager.web.recipeingredient.UpdateRecipeIngredientRequest;
import com.maria.recipe_manager.web.exception.NotFoundException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecipeIngredientServiceTest {

    @Mock
    RecipeIngredientRepository riRepo;
    @Mock
    RecipeRepository recipeRepo;
    @Mock
    IngredientRepository ingredientRepo;
    @InjectMocks
    RecipeIngredientService service;

    private AutoCloseable mocks;

    @BeforeEach
    void init() {
        mocks = MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void cleanup() throws Exception {
        mocks.close();
    }

    // -------- helpers ----------
    private static Recipe recipe(long id, String name) {
        var r = new Recipe();
        r.setId(id);
        r.setName(name);
        return r;
    }

    private static Ingredient ing(long id, String name, String unit) {
        var i = new Ingredient();
        i.setId(id);
        i.setName(name);
        i.setUnit(unit);
        return i;
    }

    private static AddIngredientToRecipeRequest addReq(long ingredientId, BigDecimal qty) {
        var r = new AddIngredientToRecipeRequest();
        r.setIngredientId(ingredientId);
        r.setQuantity(qty);
        return r;
    }

    private static UpdateRecipeIngredientRequest updQty(BigDecimal q) {
        var r = new UpdateRecipeIngredientRequest();
        r.setQuantity(q);
        return r;
    }

    @Test
    void add_ok_persistsAndReturnsEntity() {
        long recipeId = 10L, ingredientId = 7L;
        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(recipe(recipeId, "Pasta")));
        when(ingredientRepo.findById(ingredientId)).thenReturn(Optional.of(ing(ingredientId, "Salt", "g")));
        when(riRepo.existsByRecipeAndIngredient(recipeId, ingredientId)).thenReturn(false);
        when(riRepo.save(any())).thenAnswer(inv -> {
            RecipeIngredient ri = inv.getArgument(0);
            ri.setId(111L);
            return ri;
        });

        var out = service.add(recipeId, addReq(ingredientId, new BigDecimal("3.5")));

        assertThat(out.getId()).isEqualTo(111L);
        assertThat(out.getRecipe().getId()).isEqualTo(recipeId);
        assertThat(out.getIngredient().getId()).isEqualTo(ingredientId);
        assertThat(out.getQuantity()).isEqualByComparingTo("3.5");
        verify(riRepo).save(any(RecipeIngredient.class));
    }

    @Test
    void add_duplicateIngredientInRecipe_conflict() {
        long recipeId = 10L, ingredientId = 7L;
        when(recipeRepo.findById(recipeId)).thenReturn(Optional.of(recipe(recipeId, "Pasta")));
        when(ingredientRepo.findById(ingredientId)).thenReturn(Optional.of(ing(ingredientId, "Salt", "g")));
        when(riRepo.existsByRecipeAndIngredient(recipeId, ingredientId)).thenReturn(true);

        assertThatThrownBy(() -> service.add(recipeId, addReq(ingredientId, BigDecimal.ONE)))
                .isInstanceOf(IllegalStateException.class);

        verify(riRepo, never()).save(any());
    }

    @Test
    void add_recipeNotFound_404() {
        when(recipeRepo.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.add(99L, addReq(1L, BigDecimal.ONE)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void add_ingredientNotFound_404() {
        when(recipeRepo.findById(10L)).thenReturn(Optional.of(recipe(10L, "Pasta")));
        when(ingredientRepo.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.add(10L, addReq(77L, BigDecimal.ONE)))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void getOneByRiId_ok_mapsProjection() {
        long recipeId = 10L, riId = 5L;
        // construim un view mock (interfață)
        RecipeIngredientRowView view = mock(RecipeIngredientRowView.class);
        when(view.getId()).thenReturn(riId);
        when(view.getIngredientId()).thenReturn(7L);
        when(view.getIngredientName()).thenReturn("Salt");
        when(view.getUnit()).thenReturn("g");
        when(view.getQuantity()).thenReturn(new BigDecimal("2.25"));

        when(riRepo.findOneRowByRiIdAndRecipeId(recipeId, riId)).thenReturn(Optional.of(view));

        RecipeIngredientResponse resp = service.getOneByRiId(recipeId, riId);

        assertThat(resp.getId()).isEqualTo(riId);
        assertThat(resp.getIngredientId()).isEqualTo(7L);
        assertThat(resp.getIngredientName()).isEqualTo("Salt");
        assertThat(resp.getUnit()).isEqualTo("g");
        assertThat(resp.getQuantity()).isEqualByComparingTo("2.25");
    }

    @Test
    void getOneByRiId_404() {
        when(riRepo.findOneRowByRiIdAndRecipeId(10L, 5L)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.getOneByRiId(10L, 5L))
                .isInstanceOf(NotFoundException.class);
    }

    @Test
    void list_mapsObjectArraysToResponse() {
        when(riRepo.findAllRowsForRecipe(10L)).thenReturn(List.of(
                new Object[]{1L, 7L, "Salt", "g", new BigDecimal("2.0")},
                new Object[]{2L, 8L, "Sugar", "kg", new BigDecimal("0.5")}
        ));

        var list = service.list(10L);

        assertThat(list).extracting(RecipeIngredientResponse::getIngredientName)
                .containsExactly("Salt", "Sugar");
        assertThat(list).extracting(RecipeIngredientResponse::getQuantity)
                .containsExactly(new BigDecimal("2.0"), new BigDecimal("0.5"));
    }

    @ParameterizedTest
    @CsvSource({
            // recipeId, riId, ownerRecipeId, newQty
            "10, 5, 10, 3.75"
    })
    void updateQuantity_ok(long recipeId, long riId, long ownerRecipeId, String qty) {
        // RI găsit și aparține rețetei
        var ownerRecipe = recipe(ownerRecipeId, "R");
        var ri = new RecipeIngredient();
        ri.setId(riId);
        ri.setRecipe(ownerRecipe);
        when(riRepo.findById(riId)).thenReturn(Optional.of(ri));

        service.updateQuantity(recipeId, riId, updQty(new BigDecimal(qty)));

        verify(riRepo).updateQuantity(riId, new BigDecimal(qty));
    }

    static Stream<Arguments> badUpdateOwners() {
        return Stream.of(
                // RI inexistent
                Arguments.of(10L, 5L, null),
                // RI existent dar atașat altei rețete
                Arguments.of(10L, 5L, 99L)
        );
    }

    @ParameterizedTest
    @MethodSource("badUpdateOwners")
    void updateQuantity_riAbsentOrWrongOwner_throws(long recipeId, long riId, Long ownerRecipeId) {
        if (ownerRecipeId == null) {
            when(riRepo.findById(riId)).thenReturn(Optional.empty());
        } else {
            var ri = new RecipeIngredient();
            ri.setId(riId);
            ri.setRecipe(recipe(ownerRecipeId, "Other"));
            when(riRepo.findById(riId)).thenReturn(Optional.of(ri));
        }

        assertThatThrownBy(() -> service.updateQuantity(recipeId, riId, updQty(BigDecimal.ONE)))
                .isInstanceOf(IllegalArgumentException.class);

        verify(riRepo, never()).updateQuantity(anyLong(), any());
    }

    @ParameterizedTest
    @CsvSource({
            "10, 5, 10"
    })
    void delete_ok(long recipeId, long riId, long ownerRecipeId) {
        var ri = new RecipeIngredient();
        ri.setId(riId);
        ri.setRecipe(recipe(ownerRecipeId, "R"));
        when(riRepo.findById(riId)).thenReturn(Optional.of(ri));

        service.delete(recipeId, riId);

        verify(riRepo).deleteById(riId);
    }

    static Stream<Arguments> badDeleteOwners() {
        return Stream.of(
                Arguments.of(10L, 5L, null),
                Arguments.of(10L, 5L, 99L)
        );
    }

    @ParameterizedTest
    @MethodSource("badDeleteOwners")
    void delete_riAbsentOrWrongOwner_throws(long recipeId, long riId, Long ownerRecipeId) {
        if (ownerRecipeId == null) {
            when(riRepo.findById(riId)).thenReturn(Optional.empty());
        } else {
            var ri = new RecipeIngredient();
            ri.setId(riId);
            ri.setRecipe(recipe(ownerRecipeId, "Other"));
            when(riRepo.findById(riId)).thenReturn(Optional.of(ri));
        }

        assertThatThrownBy(() -> service.delete(recipeId, riId))
                .isInstanceOf(IllegalArgumentException.class);

        verify(riRepo, never()).deleteById(anyLong());

    }
}
