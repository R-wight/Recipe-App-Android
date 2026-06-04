package com.example.rylanwight;

import java.io.Serializable;

public class Instruction implements Serializable
{
    private int recipeId;
    private String instruction;

    public Instruction(int recipeId, String instruction)
    {
        this.recipeId = recipeId;
        this.instruction = instruction;
    }
}
