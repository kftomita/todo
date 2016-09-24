package com.kftomita.android.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by kftomita on 9/23/16.
 */

public class ToDoDataSource {
    private SQLiteDatabase mDatabase;
    private SQLiteHelper mDbHelper;
    private String[] mAllColumns = { SQLiteHelper.COLUMN_ID,
            SQLiteHelper.COLUMN_TODO };

    public ToDoDataSource(Context context) {
        mDbHelper = new SQLiteHelper(context);
    }

    public void open() throws SQLException {
        mDatabase = mDbHelper.getWritableDatabase();
    }

    public void close() {
        mDbHelper.close();
    }

    public ToDo createToDo(String todo) {
        ContentValues mValues = new ContentValues();
        mValues.put(SQLiteHelper.COLUMN_TODO, todo);
        long insertId = mDatabase.insert(SQLiteHelper.TABLE_TODO, null, mValues);
        Cursor cursor = mDatabase.query(SQLiteHelper.TABLE_TODO,
                mAllColumns, SQLiteHelper.COLUMN_ID + " = " + insertId, null, null, null, null);
        cursor.moveToFirst();
        ToDo newToDo = cursorToTodo(cursor);
        cursor.close();
        return newToDo;
    }

    public void deleteToDo(ToDo todo) {
        long mId = todo.getId();
        mDatabase.delete(SQLiteHelper.TABLE_TODO, SQLiteHelper.COLUMN_ID + " = " + mId, null);
    }

    public List<ToDo> getAllComments() {
        List<ToDo> mTodos = new ArrayList<ToDo>();

        Cursor cursor = mDatabase.query(SQLiteHelper.TABLE_TODO, mAllColumns, null, null, null, null, null);

        cursor.moveToFirst();
        while (!cursor.isAfterLast()) {
            ToDo mTodo = cursorToTodo(cursor);
            mTodos.add(mTodo);
            cursor.moveToNext();
        }
        // make sure to close the cursor
        cursor.close();
        return mTodos;
    }

    private ToDo cursorToTodo(Cursor cursor) {
        ToDo mTodo = new ToDo();
        mTodo.setId(cursor.getLong(0));
        mTodo.setItem(cursor.getString(1));
        return mTodo;
    }

}
