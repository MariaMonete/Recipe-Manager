package com.maria.recipe_manager.web;

//DTO pt request->validare la input

import com.maria.recipe_manager.model.Difficulty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateRecipeRequest {

    @NotBlank
    private String name;

    @NotNull
    private Difficulty difficulty;

    @Min(1)
    private int cookTimeMinutes;

    private String steps;

    // getters/setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public int getCookTimeMinutes() { return cookTimeMinutes; }
    public void setCookTimeMinutes(int cookTimeMinutes) { this.cookTimeMinutes = cookTimeMinutes; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }
}
