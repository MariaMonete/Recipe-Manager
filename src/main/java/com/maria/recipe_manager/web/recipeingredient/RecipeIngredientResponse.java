package com.maria.recipe_manager.web;

import java.math.BigDecimal;

public class RecipeIngredientResponse {
    private Long id;
    private Long ingredientId;
    private String ingredientName;
    private String unit;
    private BigDecimal quantity;

    public RecipeIngredientResponse(Long id,Long ingredientId, String ingredientName, String unit, BigDecimal quantity) {
        this.id=id;
        this.ingredientId = ingredientId;
        this.ingredientName = ingredientName;
        this.unit = unit;
        this.quantity = quantity;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }
    public Long getIngredientId() { return ingredientId; }
    public String getIngredientName() { return ingredientName; }
    public String getUnit() { return unit; }
    public BigDecimal getQuantity() { return quantity; }
}
