package com.maria.recipe_manager.persistence.repo;

import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.persistence.repo.helper.RecipeIngredientRowView;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

public interface RecipeIngredientRepository extends CrudRepository<RecipeIngredient,Long> {
    //1)NamedQuery din model
    @Query(name = "RecipeIngredient.findByRecipeOrdered")
    List<RecipeIngredient> findByRecipeOrdered(@Param("r") Long recipeId);
    //2)namednativequeries
    @Query(name = "RecipeIngredient.byRecipeNative", nativeQuery = true)
    List<RecipeIngredient> findByRecipeNative(@Param("r") Long recipeId);

    @Query(name = "RecipeIngredient.countUsageByIngredientNative", nativeQuery = true)
    long countUsageByIngredientNative(@Param("id") Long ingredientId);
    //3)query inline
    @Query("""
        select (count(ri) > 0)
        from RecipeIngredient ri
        where ri.recipe.id = :r and ri.ingredient.id = :i
    """)
    boolean existsByRecipeAndIngredient(@Param("r") Long recipeId, @Param("i") Long ingredientId);

    @Query("""
        select ri.id, ing.id, ing.name, ing.unit, ri.quantity
        from RecipeIngredient ri
        join ri.ingredient ing
        where ri.recipe.id = :r
        order by ri.id
    """)
    List<Object[]> findAllRowsForRecipe(@Param("r") Long recipeId);

    @Query("""
        select ri.id as id,
        ing.id as ingredientId,
        ing.name as ingredientName,
        ing.unit as unit,
        ri.quantity as quantity
        from RecipeIngredient ri
        join ri.ingredient ing
        where ri.id = :riId and ri.recipe.id = :recipeId
        """)
    Optional<RecipeIngredientRowView> findOneRowByRiIdAndRecipeId(@Param("recipeId") Long recipeId,
                                                                  @Param("riId") Long riId);

    @Modifying
    @Query("update RecipeIngredient ri set ri.quantity = :q where ri.id = :id")
    void updateQuantity(@Param("id") Long riId, @Param("q") BigDecimal quantity);

    Optional<RecipeIngredient> findByRecipeIdAndIngredientId(Long recipeId, Long ingredientId);

}
