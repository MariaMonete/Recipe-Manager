package com.maria.recipe_manager.persistence;


import com.maria.recipe_manager.model.Recipe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;

import java.util.List;

//DAO manual cu EntityManager

@Repository
public class RecipeDao {

    @PersistenceContext
    private EntityManager em;

    public Recipe save(Recipe r){
        em.persist(r); //insert->seteaza id-ul
        em.flush(); //forteaza insertul
        em.refresh(r); //recteste din DB (obtinem created_at)
        return r;
    }

    public Recipe findById(Long id){
        return em.find(Recipe.class, id);
    }

    public List<Recipe> findAll(){
        return em.createQuery("select r from Recipe r order by r.id", Recipe.class).getResultList();
    }
}
