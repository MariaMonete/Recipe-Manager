package com.maria.recipe_manager.persistence.old;


import com.maria.recipe_manager.model.Recipe;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
        //folosim NamedQuery-ul de pe entitate
        return em.createNamedQuery("Recipe.findAllOrdered", Recipe.class)
                .getResultList();
    }

    //delete
    @Transactional
    public boolean deleteById(Long id){
        Recipe r=em.find(Recipe.class,id); //cautam entitatea
        if(r==null){
            return false; //nu exista
        }
        em.remove(r); //jpa va emite delete
        return true;
    }
}
