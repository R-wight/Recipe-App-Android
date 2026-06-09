package com.example.rylanwight;

import android.content.ContentValues;
import android.content.Context;
import android.database.*;
import android.database.sqlite.*;



public class RecipeDBHelper extends SQLiteOpenHelper
{
    private static final String DB_NAME = "recipe1.db";
    private static final String TABLE_NAME = "Recipes";
    private static final int DB_VERSION = 1;
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
        String sCreate = "create table " +
                TABLE_NAME + " (" +
                ID + " integer primary key autoincrement, " +
                NAME + " text not null, " +
                IS_FAVORITE + " integer not null default 0);";
        db.execSQL(sCreate);
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

    public boolean deleteRecipe(Recipe recipe)
    {
        return sqlDB.delete(TABLE_NAME, ID + " = " + recipe.getID(), null) > 0;
    }

    public Cursor getAllRecipes()
    {
        String[] sFields = new String[] {ID, NAME, IS_FAVORITE};
        return sqlDB.query(TABLE_NAME, sFields, null, null, null, null, null);
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

}
