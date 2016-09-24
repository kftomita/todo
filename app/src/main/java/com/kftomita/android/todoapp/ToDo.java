package com.kftomita.android.todoapp;

/**
 * Created by kftomita on 9/23/16.
 */

public class ToDo {

    private long mId;
    private String mTodo;

    public long getId() {
        return mId;
    }

    public void setId(long id) {
        this.mId = id;
    }

    public String getItem() {
        return mTodo;
    }

    public void setItem(String todo) {
        this.mTodo = todo;
    }

    // Will be used by the ArrayAdapter in the ListView
    @Override
    public String toString() {
        return mTodo;
    }
}
