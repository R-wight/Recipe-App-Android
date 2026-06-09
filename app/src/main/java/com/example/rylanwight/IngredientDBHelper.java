package com.example.rylanwight;
import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;
public class IngredientDBHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "recipe1.db";
    private static final String TABLE_NAME = "Ingredients";
    private static final int DB_VERSION = 1;
    public static final String ID = "_id";
    public static final String RECIPE_ID = "recipeID";
    public static final String INGREDIENT = "ingredient";
    public SQLiteDatabase sqlDB;

    public IngredientDBHelper(Context context)
    {
        super(context, DB_NAME, null, DB_VERSION);
    }
    public void close()
    {
        sqlDB.close();
    }

    @Override
    public void onCreate(SQLiteDatabase db)
    {
        String sCreate = "create table " +
                TABLE_NAME + " (" +
                ID + " integer primary key autoincrement, " +
                RECIPE_ID + " integer not null, " +
                INGREDIENT + " text not null, " +
                "FOREIGN KEY(" + RECIPE_ID + ") REFERENCES Recipes(" + ID + "));"; // db.execSQL("PRAGMA foreign_keys=ON;"); need this in the onOpen method
        db.execSQL(sCreate);
    }
    @Override
    public void onOpen(SQLiteDatabase db)
    {
        db.execSQL("PRAGMA foreign_keys=ON;");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
    {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public long createInstruction(Ingredient ingredient)
    {
        ContentValues cvs = new ContentValues();
        cvs.put(RECIPE_ID, ingredient.getRecipeId());
        cvs.put(INGREDIENT, ingredient.getIngredient());
        long autoId = sqlDB.insert(TABLE_NAME, null, cvs);
        ingredient.setRecipeId(autoId);

        return autoId;
    }

    public boolean updateInstruction(Ingredient ingredient)
    {
        if(ingredient.getID() < 0)
        {
            return false;
        }
        else
        {
            ContentValues cvs = new ContentValues();
            cvs.put(RECIPE_ID, ingredient.getRecipeId());
            cvs.put(INGREDIENT, ingredient.getIngredient());

            return sqlDB.update(TABLE_NAME, cvs, ID + " = " + ingredient.getID(), null) > 0;
        }
    }

    public boolean deleteInstruction(Instruction instruction)
    {
        return sqlDB.delete(TABLE_NAME, ID + " = " + instruction.getID(), null) > 0;
    }

    public Cursor getAllInstructions()
    {
        String[] sFields = new String[] {ID, RECIPE_ID, INGREDIENT};
        return sqlDB.query(TABLE_NAME, sFields, null, null, null, null, null);
    }

    public Cursor getInstruction(long id) throws SQLException
    {
        String[] sFields = new String[] {ID, RECIPE_ID, INGREDIENT};
        Cursor ingrCursor = sqlDB.query(true, TABLE_NAME, sFields, ID + " = " + id, null, null, null, null, null);
        if(ingrCursor != null)
        {
            ingrCursor.moveToFirst();
        }
        return ingrCursor;
    }
}
