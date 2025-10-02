package com.maria.recipe_manager.persistence;

import com.maria.recipe_manager.model.Ingredient;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class IngredientDao {
    @PersistenceContext
    private EntityManager em;

    public Ingredient save(Ingredient i){
        em.persist(i);
        em.flush();
        em.refresh(i);
        return i;
    }

    public Ingredient findById(Long id){
        return em.find(Ingredient.class,id);
    }

    public List<Ingredient> findAll(){
        return em.createQuery("select i from Ingredient i order by i.id", Ingredient.class).getResultList();
    }

    public void delete(Ingredient i){
        em.remove(i);
    }

    public boolean existsByNameIgnoreCase(String name) {//sa nu avem ingrediente cu acelasi nume
        var list = em.createQuery(
                        "select 1 from Ingredient i where lower(i.name) = lower(:n)",
                        Integer.class
                )
                .setParameter("n", name)
                .setMaxResults(1)
                .getResultList();
        return !list.isEmpty();
    }
}
