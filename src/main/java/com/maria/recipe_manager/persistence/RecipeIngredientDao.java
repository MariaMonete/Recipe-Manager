package com.maria.recipe_manager.persistence;

import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.model.RecipeIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.List;

@Repository
public class RecipeIngredientDao {
    @PersistenceContext
    private EntityManager em;

    public Recipe mustFindRecipe(Long id) {
        Recipe r = em.find(Recipe.class, id);
        if (r == null) throw new IllegalArgumentException("Recipe not found: " + id);
        return r;
    }

    public Ingredient mustFindIngredient(Long id) {
        Ingredient i = em.find(Ingredient.class, id);
        if (i == null) throw new IllegalArgumentException("Ingredient not found: " + id);
        return i;
    }

    public RecipeIngredient save(RecipeIngredient ri){
        em.persist(ri);
        em.flush();
        em.refresh(ri);
        return ri;
    }

    public boolean existsByRecipeAndIngredient(Long recipeId, Long ingredientId) {
        var list = em.createQuery("""
            select 1 from RecipeIngredient ri
            where ri.recipe.id = :r and ri.ingredient.id = :i
        """, Integer.class)
                .setParameter("r", recipeId)
                .setParameter("i", ingredientId)
                .setMaxResults(1)
                .getResultList();
        return !list.isEmpty();
    }

    public List<Object[]> findAllRowsForRecipe(Long recipeId) {
        return em.createQuery("""
            select ri.id, ing.id, ing.name, ing.unit, ri.quantity
            from RecipeIngredient ri
            join ri.ingredient ing
            where ri.recipe.id = :r
            order by ri.id
        """, Object[].class)
                .setParameter("r", recipeId)
                .getResultList();
    }

    public RecipeIngredient findById(Long id) {
        return em.find(RecipeIngredient.class, id);
    }

    public void updateQuantity(Long riId, BigDecimal quantity) {
        em.createQuery("update RecipeIngredient ri set ri.quantity = :q where ri.id = :id")
                .setParameter("q", quantity)
                .setParameter("id", riId)
                .executeUpdate();
    }

    public void deleteById(Long id) {
        var managed = em.find(RecipeIngredient.class, id);
        if (managed != null) em.remove(managed);
    }

    public long countUsageOfIngredients(Long ingredientId){
        return em.createQuery("select count(ri) from RecipeIngredient ri where ri.ingredient.id = :id", Long.class)
                .setParameter("id",ingredientId)
                .getSingleResult();
    }

}
