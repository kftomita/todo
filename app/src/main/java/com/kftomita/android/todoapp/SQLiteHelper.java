package com.kftomita.android.todoapp;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by kftomita on 9/23/16.
 */

public class SQLiteHelper extends SQLiteOpenHelper{

    public static final String TABLE_TODO = "todos";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TODO = "todo";

    private static final String DATABASE_NAME = "todo.db";
    private static final int DATABASE_VERSION = 1;

    // Database creation sql statement
    private static final String DATABASE_CREATE = "create table "
            + TABLE_TODO + "( " + COLUMN_ID
            + " integer primary key autoincrement, " + COLUMN_TODO
            + " text not null);";

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DATABASE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TODO);
        onCreate(db);
    }

    public SQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }
}
