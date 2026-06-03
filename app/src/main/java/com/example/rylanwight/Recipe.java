package com.example.rylanwight;

import java.util.ArrayList;

public class Recipe
{
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
}
