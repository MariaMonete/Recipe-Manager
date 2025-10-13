package com.maria.recipe_manager.web.recipe;

//DTO pt request->validare la input

import com.maria.recipe_manager.model.Difficulty;
import jakarta.validation.constraints.*;

public class CreateRecipeRequest {

    @NotBlank(message = "name must not be blank")
    @Size(max = 255, message = "name length must be <= 255")
    private String name;

    @NotNull(message = "difficulty must not be blank")
    @NotNull( message = "difficulty must be one of EASY, MEDIUM, HARD")
    private Difficulty difficulty;

    @NotNull(message = "cookTimeMinutes is required")
    @Min(value = 1, message = "cookTimeMinutes must be >= 1")
    @Max(value = 1440, message = "cookTimeMinutes must be <= 1440")
    private int cookTimeMinutes;

    @Size(max = 10000, message = "steps length must be <= 10000")
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
