package com.example.rylanwight;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable
{
    //Need to add boolean for favorites eventually
    private long ID;
    private String name;
    private boolean isFavorite;
    private String imageUri;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Instruction> instructions;

    public Recipe(String name, ArrayList<Ingredient> ingredients, ArrayList<Instruction> instructions)
    {
        this.ID = -1;
        this.name = name;
        this.isFavorite = false;
        this.ingredients = ingredients;
        this.instructions = instructions;
        this.imageUri = "";
    }

    public Recipe(String name)
    {
        this.ID = -1;
        this.name = name;
        this.isFavorite = false;
        this.ingredients = null;
        this.instructions = null;
        this.imageUri = "";
    }

    public String getName()
    {
        return this.name;
    }

    public ArrayList<Ingredient> getIngredients()
    {
        return this.ingredients;
    }

    public ArrayList<Instruction> getInstructions()
    {
        return this.instructions;
    }
    public long getID()
    {
        return this.ID;
    }

    public void setID(long ID)
    {
        this.ID = ID;
    }
    public int isFavorite()
    {
        return this.isFavorite ? 1 : 0;
    }
    public void setFavorite(boolean bVal)
    {
        this.isFavorite = bVal;
    }
    public boolean getFavorite()
    {
        return this.isFavorite;
    }
    public void setIngredients(ArrayList<Ingredient> obList)
    {
        this.ingredients = obList;
    }
    public void setInstructions(ArrayList<Instruction> obList)
    {
        this.instructions = obList;
    }
    public void setImageUri(String uri)
    {
        this.imageUri = uri;
    }

    public String getImageUri()
    {
        return this.imageUri;
    }

    public void setName(String sName)
    {
        this.name = sName;
    }
}
