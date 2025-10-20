package com.maria.recipe_manager.ingredient;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.persistence.old.IngredientDao;
import com.maria.recipe_manager.persistence.repo.IngredientRepository;
import com.maria.recipe_manager.persistence.repo.RecipeIngredientRepository;
import com.maria.recipe_manager.service.IngredientService;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.ingredient.CreateIngredientRequest;
import com.maria.recipe_manager.web.ingredient.UpdateIngredientRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class IngredientServiceTest {
    // todo: tema pentru acasa: parameterized tests
    @Mock
    IngredientRepository repo;

    @Mock
    RecipeIngredientRepository riRepo;

    @InjectMocks
    IngredientService service;

    private AutoCloseable mocks;

    @BeforeEach
    void init(){
        mocks= MockitoAnnotations.openMocks(this);
    }

    @AfterEach
    void teardown() throws Exception{
        mocks.close();
    }

    // helpers
    private static CreateIngredientRequest cReq(String name, String unit) {
        var r = new CreateIngredientRequest(); r.setName(name); r.setUnit(unit); return r;
    }
    private static UpdateIngredientRequest uReq(String name, String unit) {
        var r = new UpdateIngredientRequest(); r.setName(name); r.setUnit(unit); return r;
    }
    private static Ingredient ing(Long id, String name, String unit) {
        var i = new Ingredient(); i.setId(id); i.setName(name); i.setUnit(unit); return i;
    }

    // 1) create: nume duplicat (case-insensitive) - ValueSource
    @ParameterizedTest
    @ValueSource(strings = {"Salt","salt","SALT","SaLT"})
    void create_conflict_nameAlreadyExists(String name){
        when(repo.existsByNameIgnoreCase(name)).thenReturn(true);

        assertThatThrownBy(()->service.create(cReq(name,"g"))).isInstanceOf(IllegalArgumentException.class);

        verify(repo).existsByNameIgnoreCase(name);
        verify(repo, never()).save(any());
        clearInvocations(repo);
    }

    // 2) get: 404 pentru diverse ID-uri inexistente - ValueSource
    @ParameterizedTest
    @ValueSource(longs = {0L, 7L, 999L})
    void get_404_notFound(long id) {
        when(repo.findById(id)).thenReturn(Optional.empty());
        assertThatThrownBy(() -> service.get(id)).isInstanceOf(NotFoundException.class);
        verify(repo).findById(id);
        clearInvocations(repo);
    }

    // 3) delete: conflict cand ingredientul e folosit - ValueSource pentru count > 0
    @ParameterizedTest
    @ValueSource(longs = {1L, 2L, 100L})
    void delete_conflict_whenUsedByRecipes(long usageCount) {
        long id = 5L;
        when(riRepo.countUsageByIngredientNative(id)).thenReturn(usageCount);

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(IllegalStateException.class);

        verify(riRepo).countUsageByIngredientNative(id);
        verify(repo, never()).existsById(anyLong());
        verify(repo, never()).deleteById(anyLong());
        clearInvocations(repo, riRepo);
    }

    // 4) update: conflict cand numele e deja folosit de alt ingredient - CsvSource
    @ParameterizedTest
    @CsvSource({
            "1, Salt, g",
            "10, Sugar, kg",
            "42, Oil, ml"
    })
    void update_conflict_nameInUse(long id, String newName, String unit) {
        when(repo.findById(id)).thenReturn(Optional.of(ing(id, "Old", unit)));
        when(repo.existsOtherWithName(id, newName)).thenReturn(true);

        assertThatThrownBy(() -> service.update(id, uReq(newName, unit)))
                .isInstanceOf(IllegalArgumentException.class);

        verify(repo, never()).save(any());
        clearInvocations(repo);
    }

    // 5) search native: diferite inputuri - MethodSource
    static Stream<Arguments> searchInputs() {
        return Stream.of(
                Arguments.of("sa", List.of(ing(1L, "Salt", "g"))),
                Arguments.of("su", List.of(ing(2L, "Sugar", "kg"))),
                Arguments.of("xx", List.of())
        );
    }

    @ParameterizedTest
    @MethodSource("searchInputs")
    void searchByNameNative_variants(String q, List<Ingredient> expected) {
        when(repo.findAllNative(q)).thenReturn(expected);

        var list = service.searchByNameNative(q);

        assertThat(list).containsExactlyElementsOf(expected);
        verify(repo).findAllNative(q);
        clearInvocations(repo);
    }

    // 6) un test simplu pozitiv (non-parametrizat) pentru create ok (exemplu)
    @Test
    void create_ok_persists() {
        when(repo.existsByNameIgnoreCase("Salt")).thenReturn(false);
        when(repo.save(any())).thenAnswer(inv -> {
            Ingredient saved = inv.getArgument(0);
            saved.setId(100L);
            return saved;
        });

        var out = service.create(cReq("Salt", "g"));

        assertThat(out.getId()).isEqualTo(100L);
        assertThat(out.getName()).isEqualTo("Salt");
        assertThat(out.getUnit()).isEqualTo("g");
        verify(repo).save(any(Ingredient.class));
    }

    // 7) delete: 404 cand nu exista (pozitiv/negativ combinat)
    @ParameterizedTest
    @ValueSource(longs = {11L, 12L})
    void delete_404_whenNotExists(long id) {
        when(riRepo.countUsageByIngredientNative(id)).thenReturn(0L);
        when(repo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).deleteById(anyLong());
        clearInvocations(repo, riRepo);
    }

    // 8) listAll: ordinea din repo
    @Test
    void listAll_returnsOrderedFromRepo() {
        when(repo.findAllOrdered()).thenReturn(List.of(ing(1L, "A", "g"), ing(2L, "B", "ml")));
        var list = service.listAll();
        assertThat(list).extracting(Ingredient::getName).containsExactly("A", "B");
        verify(repo).findAllOrdered();
    }

    // 9) update ok (pozitiv)
    @Test
    void update_ok_persistsChanges() {
        var existing = ing(1L, "Old", "g");
        when(repo.findById(1L)).thenReturn(Optional.of(existing));
        when(repo.existsOtherWithName(1L, "Salt")).thenReturn(false);
        when(repo.save(any())).thenAnswer(inv -> inv.getArgument(0));

        var out = service.update(1L, uReq("Salt", "g"));

        assertThat(out.getName()).isEqualTo("Salt");
        assertThat(out.getUnit()).isEqualTo("g");
        verify(repo).save(any(Ingredient.class));
    }

}
