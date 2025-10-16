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

    // TODO 1:
    // @Query
    // @NamedQuery
    // @NativeQuery // pe entitati

    // TODO 2: spring data jpa cu CrudRepository

    // TODO 3: use scheduled jobs. la fiecare ora, vreau un log cu cate ingrediente ai in baza de date.

    // TODO: integrare cu rabbitmq pentru primit retete noi.

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

    public Ingredient merge(Ingredient i){
        return em.merge(i);
    }

    public void deleteById(Long id){
        var ing=em.find(Ingredient.class,id);
        if(ing!=null) em.remove(ing);
    }

    public boolean existsOtherWithName(Long id, String name){
        var list=em.createQuery("select 1 from Ingredient i where lower(i.name)=lower(:n) and i.id <> :id", Integer.class)
                .setParameter("n",name)
                .setParameter("id",id)
                .setMaxResults(1)
                .getResultList();
        return !list.isEmpty();
    }
}
