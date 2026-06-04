package com.example.rylanwight;

import java.io.Serializable;
import java.util.ArrayList;

public class Recipe implements Serializable
{
    //Need to add boolean for favorites eventually
    private String name;
    private ArrayList<Ingredient> ingredients;
    private ArrayList<Instruction> instructions;

    public Recipe(String name, ArrayList<Ingredient> ingredients, ArrayList<Instruction> instructions)
    {
        this.name = name;
        this.ingredients = ingredients;
        this.instructions = instructions;
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
}
