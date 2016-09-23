package com.kftomita.android.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.util.ArrayList;

public class EditItemActivity extends AppCompatActivity {
    EditText mEditText;
    Button mEditButton;
    ArrayList<String> mItems;
    Integer mPosition;
    String mItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_item);

        mEditText = (EditText)findViewById(R.id.editEditText);
        mEditButton = (Button) findViewById(R.id.btnEditItem);

        //get extras
        mItems = getIntent().getStringArrayListExtra("items");
        mPosition = getIntent().getIntExtra("position",0);
        mItem = mItems.get(mPosition);
        //Update TextView
        mEditText.setText(mItem);

    }

    public void onEdiItem(View view) {
        //Update Array with new value
        mItems.set(mPosition,mEditText.getText().toString());

        Intent mData = new Intent();
        mData.putExtra("items",mItems);
        setResult(RESULT_OK, mData);

        finish();
    }
}
