package com.kftomita.android.todoapp;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import static com.kftomita.android.todoapp.R.id.etEditText;

public class MainActivity extends AppCompatActivity {

    ArrayList<String> mItems;
    ArrayAdapter<String> mItemsAdapter;
    ListView mLvItems;
    EditText mEtEditText;
    private final int REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        populateArrayItems();
        mLvItems = (ListView) findViewById(R.id.lvitems);
        mLvItems.setAdapter(mItemsAdapter);
        mEtEditText = (EditText) findViewById(etEditText);

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
                        mItems.remove(pos);
                        mItemsAdapter.notifyDataSetChanged();
                        writeItems();
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
                i.putExtra("items", mItems);
                i.putExtra("position", position);
                startActivityForResult(i,REQUEST_CODE);
            }
        });
    }

    public void populateArrayItems(){
        readItems();
        mItemsAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, mItems);
    }

    public void onAddItem(View view) {
        mItemsAdapter.add(mEtEditText.getText().toString());
        mEtEditText.setText("");
        writeItems();
        //hide keyboard
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

        Toast.makeText(getApplicationContext(), getResources().getString(R.string.added), Toast.LENGTH_SHORT).show();
    }

    private void readItems(){
        File mFilesDir = getFilesDir();
        File mFile = new File(mFilesDir, "todo.txt");
        if(mFile.exists()) {
            try {
                mItems = new ArrayList<String>(FileUtils.readLines(mFile));
            } catch (IOException e) {
                System.out.println("IOException:" + e);
            }
        } else{
            mItems = new ArrayList<String>();
        }
    }

    private void writeItems(){
        File mFilesDir = getFilesDir();
        File mFile = new File(mFilesDir, "todo.txt");
        try{
            FileUtils.writeLines(mFile, mItems);
        } catch (IOException e){

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == REQUEST_CODE) {
            mItems.clear();
            mItems.addAll(data.getExtras().getStringArrayList("items"));
            mItemsAdapter.notifyDataSetChanged();
            writeItems();
        }
    }
}
