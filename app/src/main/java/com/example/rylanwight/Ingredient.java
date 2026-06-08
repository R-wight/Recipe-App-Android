package com.example.rylanwight;

import java.io.Serializable;

public class Ingredient implements Serializable
{
    private long id;
    private long recipeId;
    private String ingredient;
    public Ingredient(long recipeId, String ingredient)
    {
        this.id = -1;
        this.recipeId = recipeId;
        this.ingredient = ingredient;
    }

    public String getIngredient()
    {
        return this.ingredient;
    }

    public long getRecipeId()
    {
        return this.recipeId;
    }

    public void setRecipeId(long id)
    {
        this.recipeId = id;
    }

    public long getID()
    {
        return this.id;
    }

}
