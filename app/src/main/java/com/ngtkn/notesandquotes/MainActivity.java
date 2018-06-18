package com.ngtkn.notesandquotes;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final String ADD = "ADD";
    private static final String EDIT = "EDIT";
    private RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<String> noteList = new ArrayList<>();
    Notes notes;
    private String newEntry = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<String>());
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        notes = new Notes();

        loadNotes();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if (id == R.id.action_add) {
            editNotes("Add New", ADD);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void editNotes(String title, String action){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setSingleLine(false);  //add this
        input.setLines(4);
        input.setMaxLines(5);
        input.setGravity(Gravity.START | Gravity.TOP);
        input.setHorizontalScrollBarEnabled(false); //this
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newEntry = input.getText().toString();
                if(!TextUtils.isEmpty(newEntry)){
                    notes.addNewNote(newEntry);
                    recyclerViewAdapter.loadNewData(notes.getNotes());
                }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();
    }

    private void loadNotes(){
        Log.d(TAG, "loadNotes: starts");
        notes.addNewNote("1");
        notes.addNewNote("2");
        notes.addNewNote("3");
        notes.addNewNote("4");
        notes.addNewNote("5");

        recyclerViewAdapter.loadNewData(notes.getNotes());
    }
}
