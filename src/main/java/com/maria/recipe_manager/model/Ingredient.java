package com.maria.recipe_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@Entity
@Table(name="ingredients", uniqueConstraints = @UniqueConstraint(name="uk_ingredient_name", columnNames = "name"))
@NamedQuery(
        name="Ingredient.findAllOrdered",
        query="select i from Ingredient i order by i.id"
)
@NamedNativeQuery(
        name="Ingredient.findAllNative",
        query= """
            select *
            from ingredients
            where lower(name) like lower(concat('%', :name, '%'))
            order by id
        """,
        resultClass = Ingredient.class
)
public class Ingredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max=128)
    @Column(nullable = false,length=128,unique=true)
    private String name;

    @NotBlank
    @Size(max=16)
    @Column(nullable = false,length = 16)
    private String unit;

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }
}
