package com.maria.recipe_manager.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;

import java.math.BigDecimal;

@Entity
@Table(name="recipe_ingredients")
public class RecipeIngredient {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false,fetch=FetchType.LAZY)
    @JoinColumn(name="recipe_id",nullable = false)
    private Recipe recipe;

    @ManyToOne(optional = false,fetch = FetchType.LAZY)
    @JoinColumn(name="ingredient_id",nullable = false)
    private Ingredient ingredient;

    @DecimalMin("0.01")
    @Column(nullable = false,precision = 10,scale = 2)
    private BigDecimal quantity;

    public Long getId(){return id;}
    public void setId(Long id){this.id=id;}

    public Recipe getRecipe(){return recipe;}
    public void setRecipe(Recipe recipe){this.recipe=recipe;}

    public Ingredient getIngredient(){return ingredient;}
    public void setIngredient(Ingredient ingredient){this.ingredient=ingredient;}

    public BigDecimal getQuantity(){return quantity;}
    public void setQuantity(BigDecimal quantity){this.quantity=quantity;}
}
