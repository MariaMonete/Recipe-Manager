package com.maria.recipe_manager.recipe;

import com.maria.recipe_manager.model.Difficulty;
import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.persistence.old.RecipeDao;
import com.maria.recipe_manager.persistence.repo.RecipeRepository;
import com.maria.recipe_manager.service.RecipeService;
import com.maria.recipe_manager.web.exception.NotFoundException;
import com.maria.recipe_manager.web.recipe.CreateRecipeRequest;
import com.maria.recipe_manager.web.recipe.PatchRecipeRequest;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class RecipeServiceTest {
    @Mock
    RecipeRepository repo;

    @InjectMocks
    RecipeService service;

    private AutoCloseable mocks;

    @BeforeEach
    void init() { mocks = MockitoAnnotations.openMocks(this); }

    @AfterEach
    void teardown() throws Exception { mocks.close(); }

    // -------- helpers ----------
    private static CreateRecipeRequest cReq(String name, Difficulty d, int cook, String steps) {
        var r = new CreateRecipeRequest();
        r.setName(name);
        r.setDifficulty(d);
        r.setCookTimeMinutes(cook);
        r.setSteps(steps);
        return r;
    }

    private static PatchRecipeRequest pReq(String name, Difficulty d, Integer cook, String steps) {
        var r = new PatchRecipeRequest();
        r.setName(name);
        r.setDifficulty(d);
        r.setCookTimeMinutes(cook);
        r.setSteps(steps);
        return r;
    }

    private static Recipe rec(Long id, String name, Difficulty d, int cook, String steps) {
        var r = new Recipe();
        r.setId(id);
        r.setName(name);
        r.setDifficulty(d);
        r.setCookTimeMinutes(cook);
        r.setSteps(steps);
        return r;
    }

    @ParameterizedTest
    @CsvSource({
            "Pasta, EASY, 12, Boil water...",
            "Soup,  MEDIUM, 25, Simmer gently",
            "Pie,   HARD,  60, Bake"
    })
    void create_persists_and_refetches(String name, Difficulty diff, int cook, String steps) {
        // save returns entity with id; then service face findById pentru re-fetch
        when(repo.save(any())).thenAnswer(inv -> {
            Recipe saved = inv.getArgument(0);
            saved.setId(101L);
            return saved;
        });
        when(repo.findById(101L)).thenReturn(Optional.of(rec(101L, name, diff, cook, steps)));

        var out = service.create(cReq(name, diff, cook, steps));

        assertThat(out.getId()).isEqualTo(101L);
        assertThat(out.getName()).isEqualTo(name);
        assertThat(out.getDifficulty()).isEqualTo(diff);
        assertThat(out.getCookTimeMinutes()).isEqualTo(cook);
        assertThat(out.getSteps()).isEqualTo(steps);

        verify(repo).save(any(Recipe.class));
        verify(repo).findById(101L);
    }

    @Test
    void create_when_refetch_empty_returns_saved_instance() {
        when(repo.save(any())).thenAnswer(inv -> {
            Recipe saved = inv.getArgument(0);
            saved.setId(202L);
            return saved;
        });
        when(repo.findById(202L)).thenReturn(Optional.empty()); // orElse(saved)

        var out = service.create(cReq("Pasta", Difficulty.EASY, 10, "Steps"));

        assertThat(out.getId()).isEqualTo(202L);
        verify(repo).save(any(Recipe.class));
        verify(repo).findById(202L);
    }

    @Test
    void listAll_returnsNamedQueryOrder() {
        when(repo.findAllOrdered()).thenReturn(List.of(
                rec(1L, "A", Difficulty.EASY, 10, "S"),
                rec(2L, "B", Difficulty.MEDIUM, 20, "S")));

        var list = service.listAll();

        assertThat(list).extracting("name").containsExactly("A", "B");

        verify(repo).findAllOrdered();
    }

    @ParameterizedTest
    @ValueSource(longs = {0L, 7L, 999L})
    void getById_404(long id) {
        when(repo.findById(id)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getById(id))
                .isInstanceOf(NotFoundException.class);

        verify(repo).findById(id);
    }

    @Test
    void getById_ok() {
        when(repo.findById(5L)).thenReturn(Optional.of(rec(5L, "Pie", Difficulty.HARD, 60, "Bake")));

        var out = service.getById(5L);

        assertThat(out.getName()).isEqualTo("Pie");
        verify(repo).findById(5L);
    }

    @ParameterizedTest
    @ValueSource(longs = {10L, 11L})
    void delete_404_whenNotExists(long id) {
        when(repo.existsById(id)).thenReturn(false);

        assertThatThrownBy(() -> service.delete(id))
                .isInstanceOf(NotFoundException.class);

        verify(repo, never()).deleteById(anyLong());
    }

    @ParameterizedTest
    @ValueSource(longs = {21L, 22L, 23L})
    void delete_ok(long id) {
        when(repo.existsById(id)).thenReturn(true);

        service.delete(id);

        verify(repo).deleteById(id);
    }

    @ParameterizedTest
    @CsvSource({
            "1, Pasta, EASY, 12, Boil",
            "2, Soup, MEDIUM, 20, Simmer",
            "3, Pie, HARD, 60, Bake"
    })
    void update_ok_sets_all_fields(long id, String name, Difficulty diff, int cook, String steps) {
        var existing = rec(id, "Old", Difficulty.MEDIUM, 5, "X");
        when(repo.findById(id)).thenReturn(Optional.of(existing));

        var out = service.update(id, cReq(name, diff, cook, steps));

        assertThat(out.getId()).isEqualTo(id);
        assertThat(out.getName()).isEqualTo(name);
        assertThat(out.getDifficulty()).isEqualTo(diff);
        assertThat(out.getCookTimeMinutes()).isEqualTo(cook);
        assertThat(out.getSteps()).isEqualTo(steps);
        verify(repo).findById(id);
        verify(repo, never()).save(any());
    }

    @Test
    void update_404() {
        when(repo.findById(77L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.update(77L, cReq("X", Difficulty.EASY, 1, "S")))
                .isInstanceOf(NotFoundException.class);
    }

    static Stream<Arguments> patchCases() {
        return Stream.of(
                Arguments.of(pReq("  Pasta  ", null, null, null),
                        (Consumer<Recipe>) r -> assertThat(r.getName()).isEqualTo("Pasta")),

                Arguments.of(pReq(null, Difficulty.HARD, null, null),
                        (Consumer<Recipe>) r -> assertThat(r.getDifficulty()).isEqualTo(Difficulty.HARD)),

                Arguments.of(pReq(null, null, 45, null),
                        (Consumer<Recipe>) r -> assertThat(r.getCookTimeMinutes()).isEqualTo(45)),

                Arguments.of(pReq(null, null, null, "Chop"),
                        (Consumer<Recipe>) r -> assertThat(r.getSteps()).isEqualTo("Chop")),

                Arguments.of(pReq("  Soup  ", Difficulty.MEDIUM, 25, "Simmer"),
                        (Consumer<Recipe>) r -> {
                            assertThat(r.getName()).isEqualTo("Soup");
                            assertThat(r.getDifficulty()).isEqualTo(Difficulty.MEDIUM);
                            assertThat(r.getCookTimeMinutes()).isEqualTo(25);
                            assertThat(r.getSteps()).isEqualTo("Simmer");
                        })
        );
    }

    @ParameterizedTest
    @MethodSource("patchCases")
    void patch_applies_only_non_null_fields(PatchRecipeRequest pr, java.util.function.Consumer<Recipe> asserter) {
        var existing = rec(5L, "Old", Difficulty.EASY, 10, "S");
        when(repo.findById(5L)).thenReturn(Optional.of(existing));

        var out = service.patch(5L, pr);

        assertThat(out.getId()).isEqualTo(5L);
        asserter.accept(out);

        verify(repo).findById(5L);
        verify(repo, never()).save(any());
    }

    @Test
    void patch_404() {
        when(repo.findById(404L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.patch(404L, pReq(null, null, null, null)))
                .isInstanceOf(NotFoundException.class);
    }

    static Stream<Arguments> searchCases() {
        return Stream.of(
                Arguments.of("pa", List.of(rec(1L, "Pasta", Difficulty.EASY, 10, "S"))),
                Arguments.of("so", List.of(rec(2L, "Soup", Difficulty.MEDIUM, 20, "S"))),
                Arguments.of("zz", List.of())
        );
    }

    @ParameterizedTest
    @MethodSource("searchCases")
    void searchByNameNative_delegates(String q, List<Recipe> expected) {
        when(repo.searchByNameNative(q)).thenReturn(expected);

        var list = service.searchByNameNative(q);

        assertThat(list).containsExactlyElementsOf(expected);
        verify(repo).searchByNameNative(q);
    }
}
