package com.maria.recipe_manager.web;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class CreateIngredientRequest {
    @NotBlank @Size(max=128)
    @Size(max = 128, message = "name length must be <= 128")
    private String name;

    @NotBlank @Size(max=16, message = "unit length must be <= 16")
    @Pattern(regexp = "g|ml|pcs", message = "unit must be one of: g, ml, pcs")
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
