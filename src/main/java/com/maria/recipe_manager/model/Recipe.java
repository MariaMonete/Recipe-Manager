package com.maria.recipe_manager.model;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDateTime;

@Entity
@Table(name="recipes")
public class Recipe {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //corespunde bigserial
    private Long id;

    @NotBlank
    @Column(nullable = false,length = 255)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false,length = 16)
    private Difficulty difficulty;

    @Min(1)
    @Column(name = "cook_time_minutes", nullable = false)
    private int cookTimeMinutes;

    @Column(columnDefinition = "text", nullable = false)
    @JdbcTypeCode(SqlTypes.LONGVARCHAR)
    private String steps;

    @Column(name="created_at",insertable=false,updatable=false)
    private LocalDateTime createdAt;

    //getteri si setteri
    public Long getId(){return id;}
    public void setId(Long id) {this.id=id;}

    public String getName(){return name;}
    public void setName(String name){this.name=name;}

    public Difficulty getDifficulty() { return difficulty; }
    public void setDifficulty(Difficulty difficulty) { this.difficulty = difficulty; }

    public int getCookTimeMinutes() { return cookTimeMinutes; }
    public void setCookTimeMinutes(int cookTimeMinutes) { this.cookTimeMinutes = cookTimeMinutes; }

    public String getSteps() { return steps; }
    public void setSteps(String steps) { this.steps = steps; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

}
