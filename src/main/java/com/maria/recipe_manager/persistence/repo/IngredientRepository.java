package com.maria.recipe_manager.persistence.repo;

import com.maria.recipe_manager.model.Ingredient;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface IngredientRepository extends CrudRepository<Ingredient,Long> {
   //1) namedquery din model
    @Query(name="Ingredient.findAllOrdered")
    List<Ingredient> findAllOrdered();
    //2)namednativequery din model
    @Query(name = "Ingredient.findAllNative", nativeQuery = true)
    List<Ingredient> findAllNative(@Param("name") String name);

    //3)query jpql inline pt existente

    @Query("""
        select (count(i) > 0)
        from Ingredient i
        where lower(i.name) = lower(:name)
    """)
    boolean existsByNameIgnoreCase(@Param("name") String name);

    @Query("""
        select (count(i) > 0)
        from Ingredient i
        where lower(i.name) = lower(:name) and i.id <> :id
    """)
    boolean existsOtherWithName(@Param("id") Long id, @Param("name") String name);
}
