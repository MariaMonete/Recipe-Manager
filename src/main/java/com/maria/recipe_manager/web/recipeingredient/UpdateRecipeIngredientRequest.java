package com.maria.recipe_manager.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateRecipeIngredientRequest {
    @NotNull(message = "quantity is required")
    @DecimalMin(value = "0.01", message = "quantity must be > 0")
    private BigDecimal quantity;

    public BigDecimal getQuantity(){return quantity;}
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
