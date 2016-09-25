package com.kftomita.android.todoapp;

import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import static com.kftomita.android.todoapp.R.id.etEditText;

public class MainActivity extends AppCompatActivity {

    ArrayList<ToDo> mItems;
    ArrayAdapter<ToDo> mItemsAdapter;
    ListView mLvItems;
    EditText mEtEditText;
    private final int REQUEST_CODE = 100;
    private SQLiteHelper mDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mDbHelper = new SQLiteHelper(this);
        mItems = new ArrayList<ToDo>();

        populateArrayItems();
        mLvItems = (ListView) findViewById(R.id.lvitems);
        mEtEditText = (EditText) findViewById(etEditText);
        mLvItems.setAdapter(mItemsAdapter);
        //hide keyboard
        getWindow().setSoftInputMode(
                WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN
        );

        mLvItems.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                final int pos = position;
                AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
                alert.setTitle("Alert!");
                alert.setMessage("Are you sure you want to delete this row?");
                alert.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        ToDo mValue = mItems.get(pos);
                        String mValueName = mValue.getItem();

                        //Remove items from array
                        mItems.remove(pos);
                        mItemsAdapter.notifyDataSetChanged();

                        //Remove item from db
                        String selection = SQLiteHelper.COLUMN_TODO + " LIKE ?";
                        String[] args = {mValueName};
                        SQLiteDatabase db = mDbHelper.getReadableDatabase();
                        db.delete(SQLiteHelper.TABLE_TODO,selection,args);

                        Toast.makeText(getApplicationContext(), getResources().getString(R.string.deleted), Toast.LENGTH_SHORT).show();

                        dialog.dismiss();

                    }
                });
                alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        dialog.dismiss();
                    }
                });

                alert.show();


                return true;
            }
        });

        mLvItems.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent i = new Intent(view.getContext(), EditItemActivity.class);
                i.putExtra("item", mItems.get(position).toString());
                i.putExtra("position", position);
                startActivityForResult(i,REQUEST_CODE);
            }
        });
    }

    public void populateArrayItems(){
        //Fetch data from db
        SQLiteDatabase db = mDbHelper.getReadableDatabase();
        String[] mProjection = {
                SQLiteHelper.COLUMN_TODO
        };

        String mSortOrder = SQLiteHelper.COLUMN_ID + " ASC";
        Cursor c = db.query(SQLiteHelper.TABLE_TODO, mProjection,null, null, null, null, mSortOrder);

        if(c.getCount() > 0) {
            while (c.moveToNext()) {
                String mValue = c.getString(c.getColumnIndex(SQLiteHelper.COLUMN_TODO));
                ToDo mTodo = new ToDo();
                mTodo.setItem(mValue);
                mItems.add(mTodo);
            }
        }else{
            System.out.println("No data");
        }
        c.close();

        mItemsAdapter = new ArrayAdapter<ToDo>(this, android.R.layout.simple_list_item_1, mItems);
    }

    public void onAddItem(View view) {
        String mAddValue = mEtEditText.getText().toString();
        if(mAddValue.isEmpty()){
            AlertDialog.Builder alert = new AlertDialog.Builder(view.getContext());
            alert.setTitle("Alert!");
            alert.setMessage("You must inform a value");
            alert.setNeutralButton("OK", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                     dialog.dismiss();
                }
            });
            alert.show();
        }else {
            ToDo mTodo = new ToDo();
            mTodo.setItem(mAddValue);
            mItemsAdapter.add(mTodo);
            mEtEditText.setText("");

            //Add to database
            SQLiteDatabase db = mDbHelper.getWritableDatabase();
            ContentValues mValues = new ContentValues();
            mValues.put(SQLiteHelper.COLUMN_TODO,mAddValue);
            db.insert(SQLiteHelper.TABLE_TODO, null, mValues);

            //hide keyboard
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

            Toast.makeText(getApplicationContext(), getResources().getString(R.string.added), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            String mPrevious = data.getExtras().getString("previous_item");
            String mNewItem = data.getExtras().getString("item");
            int mPosition = data.getExtras().getInt("position");

            //Update array
            ToDo mTodo = new ToDo();
            mTodo.setItem(mNewItem);
            mItems.set(mPosition,mTodo);
            mItemsAdapter.notifyDataSetChanged();

            //Update Database
            SQLiteDatabase db = mDbHelper.getReadableDatabase();
            ContentValues mValues = new ContentValues();
            mValues.put(SQLiteHelper.COLUMN_TODO,mNewItem);
            String selection = SQLiteHelper.COLUMN_TODO + " LIKE ?";
            String[] args = {mPrevious};

            db.update(SQLiteHelper.TABLE_TODO,mValues, selection,args);

        }
    }
}
