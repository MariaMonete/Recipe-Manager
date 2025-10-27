package com.maria.recipe_manager.persistence.repo;

import com.maria.recipe_manager.model.Recipe;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RecipeRepository extends CrudRepository<Recipe,Long> {
    @Query(name="Recipe.findAllOrdered")
    List<Recipe> findAllOrdered();

    @Query(name="Recipe.searchByNameNative", nativeQuery = true)
    List<Recipe> searchByNameNative(@Param("name") String name);

    Optional<Recipe> findByExternalId(String externalId);
}
