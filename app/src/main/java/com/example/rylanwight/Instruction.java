package com.example.rylanwight;

import java.io.Serializable;

public class Instruction implements Serializable
{
    private long id;
    private long recipeId;
    private String instruction;

    public Instruction(long recipeId, String instruction)
    {
        this.id = -1;
        this.recipeId = recipeId;
        this.instruction = instruction;
    }

    public String getInstruction()
    {
        return this.instruction;
    }

    public long getRecipeID()
    {
        return this.recipeId;
    }

    public void setRecipeId(long id)
    {
        recipeId = id;
    }

    public long getID()
    {
        return this.id;
    }
}
