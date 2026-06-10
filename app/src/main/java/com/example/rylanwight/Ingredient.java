package com.example.rylanwight;

import java.io.Serializable;
import java.util.prefs.PreferenceChangeEvent;

public class Ingredient implements Serializable
{
    private long id;
    private long recipeId;
    private String ingredient;
    public Ingredient(String ingredient)
    {
        this.id = -1;
        this.recipeId = -1;
        this.ingredient = ingredient;
    }
    public Ingredient(long id, long recipeId, String ingredient)
    {
        this.id = id;
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
