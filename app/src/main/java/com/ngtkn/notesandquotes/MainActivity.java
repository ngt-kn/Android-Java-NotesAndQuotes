package com.ngtkn.notesandquotes;

import android.content.DialogInterface;
import android.content.Intent;
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
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private final String NEW_ENTRY = "NEW_ENTRY";
    private RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<String> noteList = new ArrayList<>();
    Notes notes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        recyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<String>());
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));


        notes = new Notes(noteList);

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
            Intent intent = new Intent(this, AddNew.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void loadNotes(){
        Log.d(TAG, "loadNotes: starts");
        notes.addNewNote("1");
        notes.addNewNote("2");
        notes.addNewNote("3");
        notes.addNewNote("4");
        notes.addNewNote("5");
//        notes.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
//                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
//                "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
//                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
//                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
//                "qui officia deserunt mollit anim id est laborum");
//        notes.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
//                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
//                "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
//                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
//                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
//                "qui officia deserunt mollit anim id est laborum");
//        notes.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
//                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
//                "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
//                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
//                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
//                "qui officia deserunt mollit anim id est laborum");
//        notes.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
//                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
//                "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
//                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
//                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
//                "qui officia deserunt mollit anim id est laborum");
//        notes.add("Lorem ipsum dolor sit amet, consectetur adipiscing elit, sed do eiusmod tempor " +
//                "incididunt ut labore et dolore magna aliqua. Ut enim ad minim veniam, quis " +
//                "nostrud exercitation ullamco laboris nisi ut aliquip ex ea commodo consequat. " +
//                "Duis aute irure dolor in reprehenderit in voluptate velit esse cillum dolore eu " +
//                "fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, sunt in culpa " +
//                "qui officia deserunt mollit anim id est laborum");
        recyclerViewAdapter.loadNewData(notes.getNotes());
    }
}
