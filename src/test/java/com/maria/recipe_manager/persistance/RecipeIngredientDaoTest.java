package com.maria.recipe_manager.persistance;

import com.maria.recipe_manager.model.Difficulty;
import com.maria.recipe_manager.model.Ingredient;
import com.maria.recipe_manager.model.Recipe;
import com.maria.recipe_manager.model.RecipeIngredient;
import com.maria.recipe_manager.persistence.RecipeIngredientDao;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest(properties = {
        "spring.jpa.hibernate.ddl-auto=none",                                // Hibernate NU creează schema
        "spring.jpa.properties.hibernate.hbm2ddl.auto=none",                 // dublu-siguranță
        "spring.flyway.enabled=false",                                       // fără Flyway la test
        "spring.sql.init.mode=always",                                       // rulează init SQL
        "spring.sql.init.schema-locations=classpath:schema.sql",             // FORȚEAZĂ schema.sql
        "spring.datasource.url=jdbc:h2:mem:testdb;MODE=PostgreSQL;DATABASE_TO_LOWER=TRUE"
})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY) // folosește H2 in-mem
@Import(RecipeIngredientDao.class)
public class RecipeIngredientDaoTest {

    @Autowired
    EntityManager em;
    @Autowired
    RecipeIngredientDao dao;

    @Test
    void exists_and_find_rows() {
        var r = new Recipe();
        r.setName("Pasta"); r.setDifficulty(Difficulty.EASY); r.setCookTimeMinutes(10); r.setSteps("...");
        em.persist(r);

        var i = new Ingredient();
        i.setName("milk"); i.setUnit("ml");
        em.persist(i);

        var ri = new RecipeIngredient();
        ri.setRecipe(r); ri.setIngredient(i); ri.setQuantity(new BigDecimal("250"));
        em.persist(ri);
        em.flush();

        assertTrue(dao.existsByRecipeAndIngredient(r.getId(), i.getId()));

        List<Object[]> rows = dao.findAllRowsForRecipe(r.getId());
        assertEquals(1, rows.size());
        assertEquals(ri.getId(), ((Number)rows.get(0)[0]).longValue());
    }

    @Test
    void find_one_by_ids() {
        var r = new Recipe();
        r.setName("Tea"); r.setDifficulty(Difficulty.EASY); r.setCookTimeMinutes(5); r.setSteps("...");
        em.persist(r);

        var i = new Ingredient();
        i.setName("water"); i.setUnit("ml");
        em.persist(i);

        var ri = new RecipeIngredient();
        ri.setRecipe(r); ri.setIngredient(i); ri.setQuantity(new BigDecimal("200"));
        em.persist(ri);
        em.flush();

        var opt = dao.findOneRowByRiIdAndRecipeId(r.getId(), ri.getId());
        assertTrue(opt.isPresent());
        Object[] row = opt.get();
        assertEquals(ri.getId(), ((Number)row[0]).longValue());
        assertEquals(i.getId(), ((Number)row[1]).longValue());
    }
}
