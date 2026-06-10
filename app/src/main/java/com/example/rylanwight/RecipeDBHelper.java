package com.example.rylanwight;

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;



public class RecipeDBHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "recipe1.db";
    private static final String TABLE_NAME = "Recipes";
    private static final int DB_VERSION = 3;
    public static final String ID = "_id";
    public static final String NAME = "name";
    public static final String IS_FAVORITE = "isFavorite";
    public SQLiteDatabase sqlDB;

    public RecipeDBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public void open() throws SQLException{    sqlDB = this.getWritableDatabase();}
    public void close()
    {
        sqlDB.close();
    }
    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sCreateRecipe = "create table " +
                TABLE_NAME + " (" +
                ID + " integer primary key autoincrement, " +
                NAME + " text not null, " +
                IS_FAVORITE + " integer not null default 0);";

        String sCreateInstruction = "create table instructions (" +
                                    "_id integer primary key autoincrement, " +
                                    "recipe_id integer not null, " +
                                    "instruction text not null)";

        String sCreateIngredient = "create table ingredients (" +
                                   "_id integer primary key autoincrement, " +
                                   "recipe_id integer not null, " +
                                   "ingredient text not null)";
        db.execSQL(sCreateRecipe);
        db.execSQL(sCreateInstruction);
        db.execSQL(sCreateIngredient);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long createRecipe(Recipe recipe)
    {
        ContentValues cvs = new ContentValues();

        cvs.put(NAME, recipe.getName());
        cvs.put(IS_FAVORITE, recipe.isFavorite());
        long autoId = sqlDB.insert(TABLE_NAME, null, cvs);
        recipe.setID(autoId);

        return autoId;
    }
    public long createInstruction(Instruction instruction)
    {
        ContentValues cvs = new ContentValues();
        cvs.put("recipe_id", instruction.getRecipeID());
        cvs.put("instruction", instruction.getInstruction());
        long autoId = sqlDB.insert("instructions", null, cvs);
        instruction.setRecipeId(autoId);

        return autoId;
    }

    public long createIngredient(Ingredient ingredient)
    {
        ContentValues cvs = new ContentValues();
        cvs.put("recipe_id", ingredient.getRecipeId());
        cvs.put("ingredient", ingredient.getIngredient());
        long autoId = sqlDB.insert("ingredients", null, cvs);
        ingredient.setRecipeId(autoId);

        return autoId;
    }

    public boolean updateRecipe(Recipe recipe)
    {
        if(recipe.getID() < 0)
        {
            return false;
        }
        else
        {
            ContentValues cvs = new ContentValues();
            cvs.put(NAME, recipe.getName());
            cvs.put(IS_FAVORITE, recipe.isFavorite());
            return sqlDB.update(TABLE_NAME, cvs, ID + " = " + recipe.getID(), null) > 0;
        }
    }

    public boolean updateInstruction(Instruction instruction)
    {
        if(instruction.getID() < 0)
        {
            return false;
        }
        else
        {
            ContentValues cvs = new ContentValues();
            cvs.put("recipe_id", instruction.getRecipeID());
            cvs.put("instruction", instruction.getInstruction());

            return sqlDB.update("instructions", cvs, ID + " = " + instruction.getID(), null) > 0;
        }
    }

    public boolean updateIngredient(Ingredient ingredient)
    {
        if(ingredient.getID() < 0)
        {
            return false;
        }
        else
        {
            ContentValues cvs = new ContentValues();
            cvs.put("recipe_name", ingredient.getRecipeId());
            cvs.put("ingredient", ingredient.getIngredient());

            return sqlDB.update("ingredients", cvs, ID + " = " + ingredient.getID(), null) > 0;
        }
    }

    public boolean deleteRecipe(Recipe recipe)
    {
        sqlDB.delete("instructions", "recipe_id = " + recipe.getID(), null);
        sqlDB.delete("ingredients", "recipe_id = " + recipe.getID(), null);
        return sqlDB.delete(TABLE_NAME, ID + " = " + recipe.getID(), null) > 0;
    }

    public boolean deleteInstruction(Instruction instruction)
    {
        return sqlDB.delete("instructions", ID + " = " + instruction.getID(), null) > 0;
    }

    public boolean deleteIngredient(Ingredient ingredient)
    {
        return sqlDB.delete("ingredients", ID + " = " + ingredient.getID(), null) > 0;
    }

    public Cursor getAllRecipes()
    {
        String[] sFields = new String[] {ID, NAME, IS_FAVORITE};
        return sqlDB.query(TABLE_NAME, sFields, null, null, null, null, null);
    }
    public Cursor getAllInstructions(Recipe recipe)
    {
        String[] sFields = new String[] {ID, "recipe_id", "instruction"};
        return sqlDB.query("instructions", sFields, "recipe_id = " + recipe.getID(), null, null, null, null);
    }
    public Cursor getAllIngredients(Recipe recipe)
    {
        String[] sFields = new String[] {ID, "recipe_id", "ingredient"};
        return sqlDB.query("ingredients", sFields, "recipe_id = " + recipe.getID(), null, null, null, null);
    }

    public Cursor getAllFavorites()
    {
        String[] sFields = new String[] {ID, NAME, IS_FAVORITE};
        return sqlDB.query(TABLE_NAME, sFields, IS_FAVORITE + " = " + 1, null, null, null, null);
    }

    public Cursor getRecipe(long id) throws SQLException
    {
        String[] sFields = new String[] {ID, NAME, IS_FAVORITE};
        Cursor recipeCursor = sqlDB.query(true, TABLE_NAME, sFields, ID + " = " + id, null, null, null, null, null);
        if(recipeCursor != null)
        {
            recipeCursor.moveToFirst();
        }
        return recipeCursor;
    }

    public Cursor getInstruction(long id) throws SQLException
    {
        String[] sFields = new String[] {ID, "recipe_id", "instruction"};
        Cursor instrCursor = sqlDB.query(true, "instructions", sFields, ID + " = " + id, null, null, null, null, null);
        if(instrCursor != null)
        {
            instrCursor.moveToFirst();
        }
        return instrCursor;
    }

    public Cursor getIngredient(long id) throws SQLException
    {
        String[] sFields = new String[] {ID, "recipe_id", "instruction"};
        Cursor ingrCursor = sqlDB.query(true, "instruction", sFields, ID + " = " + id, null, null, null, null, null);
        if(ingrCursor != null)
        {
            ingrCursor.moveToFirst();
        }
        return ingrCursor;
    }

}
