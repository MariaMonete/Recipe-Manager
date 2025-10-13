package com.maria.recipe_manager.web.recipeingredient;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class AddIngredientToRecipeRequest {
    @NotNull(message = "ingredientId is required")
    private Long ingredientId;

    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.01", message = "quantity must be > 0")
    private BigDecimal quantity;

    public Long getIngredientId() {
        return ingredientId;
    }
    public void setIngredientId(Long ingredientId) {
        this.ingredientId = ingredientId;
    }

    public BigDecimal getQuantity() {
        return quantity;
    }
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
