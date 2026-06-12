package com.example.rylanwight;

import java.io.Serializable;

/**
 * This class represents an instruction to be stored in the database that corresponds to a specific recipe
 */
public class Instruction implements Serializable
{
    private long id;
    private long recipeId;
    private String instruction;

    public Instruction(String instruction)
    {
        this.id = -1;
        this.recipeId = -1;
        this.instruction = instruction;
    }

    public Instruction(long id, long recipeId, String instruction)
    {
        this.id = id;
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
