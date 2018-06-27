package com.ngtkn.notesandquotes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;


import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

public class MainActivity extends AppCompatActivity implements RecyclerClickListener.OnRecyclerClickListener {
    private static final String TAG = "MainActivity";
    private static final String ADD = "ADD";
    private static final String EDIT = "EDIT";
    private static final String ID = "NOTES";
    private final String FONT_COLOR = "FONT_COLOR";
    private final String FONT_SIZE = "FONT_SIZE";
    private RecyclerViewAdapter recyclerViewAdapter;
    private LinearLayout bottomSheetLayout;
    private BottomSheetBehavior bottomSheetBehavior;
    static Snackbar snackbar;
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

        // init bottom sheet, set state to hidden
        bottomSheetLayout = findViewById(R.id.bottom_sheet);
        bottomSheetBehavior = BottomSheetBehavior.from(bottomSheetLayout);
        bottomSheetBehavior.setState(STATE_HIDDEN);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                bottomSheetBehavior.setState(STATE_HIDDEN);
                newQuote = quotes.getNewQuote(MainActivity.this);
                snackbar = Snackbar.make(view, newQuote, Snackbar.LENGTH_LONG)
                        .setAction("Action", null);
                View snackbarView = snackbar.getView();
                TextView textView = snackbarView.findViewById(android.support.design.R.id.snackbar_text);
                textView.setMaxLines(5);
                snackbar.setAction("Display", new SnackbarDisplayListener());
                snackbar.show();
            }
        });

        // init recycler view
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
                    if(imm != null) imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
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
    public void onItemClick(View view, final int position) {
        if((null != snackbar) && (snackbar.isShown())){
            snackbar.dismiss();
        }
        bottomSheetBehavior.setState(STATE_EXPANDED);
        LinearLayout editNote = findViewById(R.id.editNote);
        final LinearLayout deleteNote = findViewById(R.id.deleteNote);
        LinearLayout displayNote = findViewById(R.id.displayNote);

        editNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editNotes("Edit", EDIT, notes.getNote(position), position);
                bottomSheetBehavior.setState(STATE_HIDDEN);
            }
        });
        deleteNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteNote(v, position);
                bottomSheetBehavior.setState(STATE_HIDDEN);
            }
        });
        displayNote.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWidgetText(MainActivity.this, notes.getNote(position));
                widgetNotePosition = position;
                bottomSheetBehavior.setState(STATE_HIDDEN);
            }
        }));
    }

    @Override
    public void onItemLongClick(final View view, final int position) {
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
        Context context = this;
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.display_widget);
        final ComponentName displayWidget = new ComponentName(context, DisplayWidget.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.widget_font_colors)
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
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remoteViews.setTextColor(R.id.appwidget_text, newColor);
                        appWidgetManager.updateAppWidget(displayWidget, remoteViews);
                        sharedPreferences.edit().putInt(FONT_COLOR, newColor).apply();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
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
        Context context = this;
        final AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        final RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.display_widget);
        final ComponentName displayWidget = new ComponentName(context, DisplayWidget.class);

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle(R.string.widget_font_sizes)
                .setSingleChoiceItems(items, items.length, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        fontSize = Integer.valueOf(items[which].toString());
                    }
                })
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        remoteViews.setTextViewTextSize(R.id.appwidget_text, TypedValue.COMPLEX_UNIT_SP, fontSize);
                        appWidgetManager.updateAppWidget(displayWidget, remoteViews);
                        sharedPreferences.edit().putInt(FONT_SIZE, fontSize).apply();
                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void deleteNote(View view, final int position){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Delete")
                .setMessage("Do you want to delete?")
                .setIcon(R.drawable.ic_delete_24dp)
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(position < notes.getSize()) {
                            notes.deleteNote(position);
                            save();
                            recyclerViewAdapter.loadNewData(notes.getNoteList());
                            if (position == widgetNotePosition) {
                                updateWidgetText(MainActivity.this, "...");
                                widgetNotePosition = -1;
                            } else if (position < widgetNotePosition) {
                                widgetNotePosition -= 1;
                            }
                        }
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
