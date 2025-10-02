package com.maria.recipe_manager.persistence;

import com.maria.recipe_manager.model.RecipeIngredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class RecipeIngredientDao {
    @PersistenceContext
    private EntityManager em;

    public RecipeIngredient save(RecipeIngredient ri){
        em.persist(ri);
        em.flush();
        em.refresh(ri);
        return ri;
    }

    public RecipeIngredient findLink(Long recipeId, Long ingredientId){
        TypedQuery<RecipeIngredient> q=em.createQuery(
                "select ri from RecipeIngredient ri "+
                    "where ri.recipe.id= :r and ri.ingredient.id= :i",
                RecipeIngredient.class
        );
        q.setParameter("r",recipeId);
        q.setParameter("i",ingredientId);
        List<RecipeIngredient> list=q.getResultList();
        return list.isEmpty() ? null : list.get(0);
    }

    public List<RecipeIngredient> listByRecipe(Long recipeId){
        return em.createQuery(
                "select ri from RecipeIngredient ri "+
                    "join fetch ri.ingredient ing "+
                    "where ri.recipe.id= :r "+
                    "order by ing.name",
                RecipeIngredient.class)
                .setParameter("r",recipeId).getResultList();
    }

    public void delete(RecipeIngredient ri){
        em.remove(ri);
    }
}
