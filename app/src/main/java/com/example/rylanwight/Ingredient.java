package com.example.rylanwight;

import java.io.Serializable;

public class Ingredient implements Serializable
{
    private int recipeId;
    private String ingredient;
    public Ingredient(int recipeId, String ingredient)
    {
        this.recipeId = recipeId;
        this.ingredient = ingredient;
    }

    public String getIngredient()
    {
        return this.ingredient;
    }

}
