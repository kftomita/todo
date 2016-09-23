package com.kftomita.android.todoapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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
                mItems.remove(position);
                mItemsAdapter.notifyDataSetChanged();
                writeItems();
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
