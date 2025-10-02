package com.maria.recipe_manager.web;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class UpdateRecipeIngredientRequest {
    @NotNull @DecimalMin("0.01")
    private BigDecimal quantity;

    public BigDecimal getQuantity(){return quantity;}
    public void setQuantity(BigDecimal quantity) {
        this.quantity = quantity;
    }
}
