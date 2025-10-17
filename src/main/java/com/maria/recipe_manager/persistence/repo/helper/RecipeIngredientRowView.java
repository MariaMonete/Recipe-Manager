package com.maria.recipe_manager.persistence.repo.helper;

public interface RecipeIngredientRowView {
    Long getId();
    Long getIngredientId();
    String getIngredientName();
    String getUnit();
    java.math.BigDecimal getQuantity();
}
