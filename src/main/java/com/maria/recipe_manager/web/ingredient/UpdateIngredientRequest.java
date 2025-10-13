package com.maria.recipe_manager.web.ingredient;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UpdateIngredientRequest {
    @NotBlank
    @Size(max=128)
    private String name;

    @NotBlank
    @Size(max = 16)
    @Pattern(regexp = "g|ml|pcs", message = "unit must be one of: g, ml, pcs")
    private String unit;

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
}
