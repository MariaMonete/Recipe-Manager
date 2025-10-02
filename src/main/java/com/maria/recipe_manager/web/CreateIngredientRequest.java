package com.maria.recipe_manager.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class CreateIngredientRequest {
    @NotBlank @Size(max=128)
    private String name;

    @NotBlank @Size(max=16)
    private String unit;

    public String getUnit() {
        return unit;
    }
    public void setUnit(String unit) {
        this.unit = unit;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
