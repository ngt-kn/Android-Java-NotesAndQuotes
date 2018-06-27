package com.ngtkn.notesandquotes;

import android.app.Dialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RemoteViews;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity implements RecyclerClickListener.OnRecyclerClickListener {
    private static final String TAG = "MainActivity";
    private static final String ADD = "ADD";
    private static final String EDIT = "EDIT";
    private static final String ID = "NOTES";
    private RecyclerViewAdapter recyclerViewAdapter;
    static private Notes notes;
    static String newEntry = "";
    static String newQuote = "";
    private static int newColor;
    private static int fontSize;
    private static int widgetNotePosition;
    static SharedPreferences sharedPreferences;
    InputMethodManager imm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        final Quotes quotes = new Quotes();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newQuote = quotes.getNewQuote(MainActivity.this);
                Snackbar snackbar = Snackbar.make(view, newQuote, Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View snackbarView = snackbar.getView();
                TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(5);
                snackbar.setAction("Display", new SnackbarDisplayListener());
                snackbar.show();
            }
        });

        RecyclerView recyclerView = findViewById(R.id.recycler_view);
        recyclerViewAdapter = new RecyclerViewAdapter(this, new ArrayList<String>());
        recyclerView.setAdapter(recyclerViewAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.addOnItemTouchListener(new RecyclerClickListener(this, recyclerView, this));

        notes = new Notes();
        loadSharedPreferences();

        if(notes.getSize() == 0){
            loadNotes();
        } else {
            recyclerViewAdapter.loadNewData(notes.getNoteList());
        }
    }

    // On click listener for snack bar, display quote in widget
    class SnackbarDisplayListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            updateWidgetText(MainActivity.this, newQuote);
            widgetNotePosition = -1;
        }
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

        switch(id) {
            case R.id.action_settings:
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                return true;
            case R.id.action_add:
                editNotes("Add New", ADD, "", 0);
                return true;
            case R.id.font_color:
                fontColorDialog();
                return true;
            case R.id.font_size:
                fontSizeDialog();
                return true;
            default:
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    // Create an alert dialog for editing or creating a note
    private void editNotes(String title, final String action, String body, final int position){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title);

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setText(body);
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
                    if(action.equals(ADD)){
                        notes.addNewNote(newEntry);
                    }
                    if(action.equals(EDIT)){
                        notes.editNote(newEntry, position);
                    }
                    recyclerViewAdapter.loadNewData(notes.getNoteList());
                    save();
                }
                imm.hideSoftInputFromWindow(input.getWindowToken(),0);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                imm.hideSoftInputFromWindow(input.getWindowToken(),0);
            }
        });

        // Show soft keyboard
        input.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus){
                    imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);
                }
            }
        });
        builder.show();
    }

    private void loadNotes(){
        // Shows once when user first starts app
        notes.addNewNote("Add a new note with + in toolbar. Generate a quote with floating button located at bottom.");
        recyclerViewAdapter.loadNewData(notes.getNoteList());
    }

    private void loadSharedPreferences(){
        sharedPreferences = getApplicationContext()
                .getSharedPreferences("com.ngtkn.notesandquotes", Context.MODE_PRIVATE);
        try {
            JSONArray jsonArray = new JSONArray(sharedPreferences.getString(ID, "[]"));
            int len = jsonArray.length();
            for (int i=0; i < len; i++){
                notes.addNewNote(jsonArray.getString(i));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void save(){
        JSONArray jsonArray = new JSONArray(notes.getNoteList());
        sharedPreferences.edit().putString(ID, jsonArray.toString()).apply();
    }

    @Override
    public void onItemClick(View view, int position) {
        // TODO: ??
    }

    @Override
    public void onItemLongClick(final View view, final int position) {
        // Set up the custom dialog for display, edit, and delete methods
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        View v = getLayoutInflater().inflate(R.layout.long_click_dialogue, null);
        Button btnDisplay = v.findViewById(R.id.btnDisplay);
        Button btnEdit = v.findViewById(R.id.btnEdit);
        Button btnDelete = v.findViewById(R.id.btnDelete);
        builder.setView(v);
        final AlertDialog dialog = builder.create();

        // Set onclicklistener for
        btnDisplay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWidgetText(MainActivity.this, notes.getNote(position));
                widgetNotePosition = position;
                dialog.dismiss();
            }
        });

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNotes("Edit", EDIT, notes.getNote(position), position);
                dialog.dismiss();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(position < notes.getSize()){
                    notes.deleteNote(position);
                    save();
                    recyclerViewAdapter.loadNewData(notes.getNoteList());
                    if(position == widgetNotePosition){
                        updateWidgetText(MainActivity.this, "...");
                        widgetNotePosition = -1;
                    } else if (position < widgetNotePosition){
                        widgetNotePosition -= 1;
                    }
                }
                dialog.dismiss();
            }
        });
        dialog.show();
    }
    void updateWidgetText(Context context, String s){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.display_widget);
        ComponentName displayWidget = new ComponentName(context, DisplayWidget.class);
        remoteViews.setTextViewText(R.id.appwidget_text, s);
        appWidgetManager.updateAppWidget(displayWidget, remoteViews);
    }

    private void fontColorDialog(){
        final CharSequence[] items = {"Red", "Blue", "Green","Yellow","White", "Gray", "Black"};
        boolean[] itemChecked = new boolean[items.length];

        Context context = this;
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.display_widget);
        final ComponentName displayWidget = new ComponentName(context, DisplayWidget.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Font colors")
                .setSingleChoiceItems(items, items.length, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch(items[which].toString()){
                            case "Red":
                                newColor = Color.RED;
                                break;
                            case "Blue":
                                newColor = Color.BLUE;
                                break;
                            case "Green":
                                newColor = Color.GREEN;
                                break;
                            case "Yellow":
                                newColor = Color.YELLOW;
                                break;
                            case "White":
                                newColor = Color.WHITE;
                                break;
                            case "Gray":
                                newColor = Color.GRAY;
                                break;
                            case "Black":
                                newColor = Color.BLACK;
                                break;
                            default:
                                newColor = Color.BLACK;
                                break;
                        }
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remoteViews.setTextColor(R.id.appwidget_text, newColor);
                        appWidgetManager.updateAppWidget(displayWidget, remoteViews);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void fontSizeDialog(){
        final CharSequence[] items = {"10", "12", "14","16","18", "24", "32"};
        boolean[] itemChecked = new boolean[items.length];

        Context context = this;
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.display_widget);
        final ComponentName displayWidget = new ComponentName(context, DisplayWidget.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Font colors")
                .setSingleChoiceItems(items, items.length, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fontSize = Integer.valueOf(items[which].toString());
                    }
                })
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remoteViews.setTextViewTextSize(R.id.appwidget_text, TypedValue.COMPLEX_UNIT_SP, fontSize);
                        appWidgetManager.updateAppWidget(displayWidget, remoteViews);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }
}

