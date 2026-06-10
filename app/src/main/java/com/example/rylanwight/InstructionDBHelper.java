//package com.example.rylanwight;
//import android.content.ContentValues;
//import android.content.Context;
//import android.database.*;
//import android.database.sqlite.*;
//public class InstructionDBHelper extends SQLiteOpenHelper
//{
//    private static final String DB_NAME = "recipe1.db";
//    private static final String TABLE_NAME = "Instructions";
//    private static final int DB_VERSION = 1;
//    public static final String ID = "_id";
//    public static final String RECIPE_ID = "recipeID";
//    public static final String INSTRUCTION = "instruction";
//    public SQLiteDatabase sqlDB;
//
//    public InstructionDBHelper(Context context)
//    {
//        super(context, DB_NAME, null, DB_VERSION);
//    }
//    public void open() throws SQLException{    sqlDB = this.getWritableDatabase();}
//    public void close()
//    {
//        sqlDB.close();
//    }
//    @Override
//    public void onCreate(SQLiteDatabase db)
//    {
//        String sCreate = "create table " +
//                TABLE_NAME + " (" +
//                ID + " integer primary key autoincrement, " +
//                RECIPE_ID + " integer not null, " +
//                INSTRUCTION + " text not null, " +
//                "FOREIGN KEY(" + RECIPE_ID + ") REFERENCES Recipes(" + ID + ")"; // db.execSQL("PRAGMA foreign_keys=ON;"); need this in the onOpen method
//        db.execSQL(sCreate);
//    }
//
//    @Override
//    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion)
//    {
//        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
//        onCreate(db);
//    }
//
//    public long createInstruction(Instruction instruction)
//    {
//        ContentValues cvs = new ContentValues();
//        cvs.put(RECIPE_ID, instruction.getRecipeID());
//        cvs.put(INSTRUCTION, instruction.getInstruction());
//        long autoId = sqlDB.insert(TABLE_NAME, null, cvs);
//        instruction.setRecipeId(autoId);
//
//        return autoId;
//    }
//
//    public boolean updateInstruction(Instruction instruction)
//    {
//        if(instruction.getID() < 0)
//        {
//            return false;
//        }
//        else
//        {
//            ContentValues cvs = new ContentValues();
//            cvs.put(RECIPE_ID, instruction.getRecipeID());
//            cvs.put(INSTRUCTION, instruction.getInstruction());
//
//            return sqlDB.update(TABLE_NAME, cvs, ID + " = " + instruction.getID(), null) > 0;
//        }
//    }
//
//    public boolean deleteInstruction(Instruction instruction)
//    {
//        return sqlDB.delete(TABLE_NAME, ID + " = " + instruction.getID(), null) > 0;
//    }
//
//    public Cursor getAllInstructions()
//    {
//        String[] sFields = new String[] {ID, RECIPE_ID, INSTRUCTION};
//        return sqlDB.query(TABLE_NAME, sFields, null, null, null, null, null);
//    }
//
//    public Cursor getInstruction(long id) throws SQLException
//    {
//        String[] sFields = new String[] {ID, RECIPE_ID, INSTRUCTION};
//        Cursor instrCursor = sqlDB.query(true, TABLE_NAME, sFields, ID + " = " + id, null, null, null, null, null);
//        if(instrCursor != null)
//        {
//            instrCursor.moveToFirst();
//        }
//        return instrCursor;
//    }
//}
