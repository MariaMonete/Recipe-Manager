package com.maria.recipe_manager.web.recipe;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.maria.recipe_manager.model.Difficulty;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Size;

//facem dto separat pt patch, pt ignorarea campurilor cu null si validarea celor not null
@JsonInclude(JsonInclude.Include.NON_NULL)
public class PatchRecipeRequest {
    @Size(max=255,message="name length must be <=255")
    private String name;

    private Difficulty difficulty;

    //folosim wrapperul Integer ca sa poata fi null
    @Min(value=1,message="cookTimeMinutes must be >=1")
    @Max(value=1440,message="cookTimeMinutes must be <=1440")
    private Integer cookTimeMinutes;

    @Size(max=10000,message = "steps length must be <=10000")
    private String steps;

    public Difficulty getDifficulty() {
        return difficulty;
    }
    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }

    public Integer getCookTimeMinutes() {
        return cookTimeMinutes;
    }
    public void setCookTimeMinutes(Integer cookTimeMinutes) {
        this.cookTimeMinutes = cookTimeMinutes;
    }

    public String getSteps() {
        return steps;
    }
    public void setSteps(String steps) {
        this.steps = steps;
    }
}
