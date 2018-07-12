package com.ngtkn.notesandquotes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import net.margaritov.preference.colorpicker.ColorPickerDialog;

import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayList;

import static android.support.design.widget.BottomSheetBehavior.STATE_EXPANDED;
import static android.support.design.widget.BottomSheetBehavior.STATE_HIDDEN;

public class MainActivity extends AppCompatActivity implements RecyclerClickListener.OnRecyclerClickListener {
    // Const keys for shared prefs
    private static final String ADD = "ADD";
    private static final String EDIT = "EDIT";
    private static final String ID = "NOTES";
    private static final String SHOW_INSTRUCTIONS = "SHOW";
    private static final String FONT_COLOR = "FONT_COLOR";
    private static final String FONT_SIZE = "FONT_SIZE";
    private static final String APP_FONT_COLOR = "APP_FONT_COLOR";
    private static final String APP_FONT_SIZE = "APP_FONT_SIZE";
    // Menus, sheets, etc
    private RecyclerViewAdapter recyclerViewAdapter;
    private BottomSheetBehavior bottomSheetBehavior;
    static Snackbar snackbar;
    // Navigation Drawer
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle toggle;
    private NavigationView navigationView;
    // Vars
    static private Notes notes;
    static String newEntry = "";
    static String newQuote = "";
    static boolean showInstructions;
    private static int newColor;
    private static int fontSize;
    private static int widgetNotePosition;
    static SharedPreferences sharedPreferences;
    InputMethodManager imm;
    ColorPickerDialog pickerDialog;

    private static AppWidgetManager appWidgetManager;
    private static RemoteViews remoteViews;
    private static ComponentName displayWidget;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Quotes quotes = new Quotes();

        navigationView = findViewById(R.id.nav_view);
        drawerLayout = findViewById(R.id.drawer_layout);
        toggle = new ActionBarDrawerToggle(this, drawerLayout, R.string.Open, R.string.Close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setupDrawerContent(navigationView);

        appWidgetManager = AppWidgetManager.getInstance(this);
        remoteViews = new RemoteViews(this.getPackageName(), R.layout.display_widget);
        displayWidget = new ComponentName(this, DisplayWidget.class);

        // init bottom sheet, set state to hidden
        LinearLayout bottomSheetLayout = findViewById(R.id.bottom_sheet);
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

        if(showInstructions){
            loadInstructions();
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

    /* Toolbar menu */
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
            default:
                break;
        }
        // For drawer action bar button
        if(toggle.onOptionsItemSelected(item)){
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /* Notes actions */
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
                        if(widgetNotePosition==position){
                            updateWidgetText(MainActivity.this, newEntry);
                        }
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

    /* Shared prefs */
    private void loadInstructions(){
        // Shows once when user first starts app
        notes.addNewNote("Add a new note with +.\n\n" +
                "Change widget font size and color with items in toolbar.\n\n" +
                "Select a note to edit, delete, or display in widget.\n\n" +
                "Generate a quote with floating button located at bottom. Select display to show in widget");
        recyclerViewAdapter.loadNewData(notes.getNoteList());
        sharedPreferences.edit().putBoolean(SHOW_INSTRUCTIONS, false).apply();
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
        showInstructions = sharedPreferences.getBoolean(SHOW_INSTRUCTIONS, true);
        recyclerViewAdapter.dynamicTextSize = sharedPreferences.getInt(APP_FONT_SIZE, 14);
        recyclerViewAdapter.dynamicTextColor =
                sharedPreferences.getInt(APP_FONT_COLOR, Color.parseColor("#000000"));

        remoteViews.setTextColor(R.id.appwidget_text, sharedPreferences.getInt(FONT_COLOR,
                Color.parseColor("#000000")));
        remoteViews.setTextViewTextSize(R.id.appwidget_text, TypedValue.COMPLEX_UNIT_SP,
                sharedPreferences.getInt(FONT_SIZE, 14));
        appWidgetManager.updateAppWidget(displayWidget, remoteViews);

    }

    private void save(){
        JSONArray jsonArray = new JSONArray(notes.getNoteList());
        sharedPreferences.edit().putString(ID, jsonArray.toString()).apply();
    }


    /* Recyclerview click listeners */
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

    /* widget */
    void updateWidgetText(Context context, String s){
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.display_widget);
        ComponentName displayWidget = new ComponentName(context, DisplayWidget.class);
        remoteViews.setTextViewText(R.id.appwidget_text, s);
        appWidgetManager.updateAppWidget(displayWidget, remoteViews);
    }

    /* Settings dialogs*/
    // selection determines if changes are made to app (1) or widget font size (2)
    private void fontSizeDialog(final int selection){
        final CharSequence[] items = {"10", "12", "14", "16","18", "24", "32"};
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
                        if (selection == 1) {
                            //TODO: save fontsize to sharedpref
                            recyclerViewAdapter.dynamicTextSize = fontSize;
                            recyclerViewAdapter.notifyDataSetChanged();
                            sharedPreferences.edit().putInt(APP_FONT_SIZE, fontSize).apply();
                        } else {
                            remoteViews.setTextViewTextSize(R.id.appwidget_text, TypedValue.COMPLEX_UNIT_SP, fontSize);
                            appWidgetManager.updateAppWidget(displayWidget, remoteViews);
                            sharedPreferences.edit().putInt(FONT_SIZE, fontSize).apply();
                        }
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

    // selection determines if changes are made to app (1) or widget font size (2)
    private void fontColorPicker(final int selection){

        Context context = this;

        int fontColor;
        if(selection==0){
            fontColor = sharedPreferences.getInt(APP_FONT_COLOR, Color.parseColor("#000000"));
        } else {
            fontColor = sharedPreferences.getInt(FONT_COLOR, Color.parseColor("#000000"));
        }

        pickerDialog = new ColorPickerDialog(MainActivity.this, fontColor);
        pickerDialog.setAlphaSliderVisible(true);
        pickerDialog.setTitle("Color Picker");

        pickerDialog.setOnColorChangedListener(new ColorPickerDialog.OnColorChangedListener() {
            @Override
            public void onColorChanged(int color) {
                if(selection == 1) {
                    recyclerViewAdapter.dynamicTextColor = color;
                    recyclerViewAdapter.notifyDataSetChanged();
                    sharedPreferences.edit().putInt(APP_FONT_COLOR, color).apply();
                } else {
                    remoteViews.setTextColor(R.id.appwidget_text, color);
                    appWidgetManager.updateAppWidget(displayWidget, remoteViews);
                    sharedPreferences.edit().putInt(FONT_COLOR, color).apply();
                }
            }
        });
        pickerDialog.show();
    }


    /* Nav drawer */
    public void selectItemDrawer(MenuItem menuItem){
        android.support.v4.app.Fragment fragment = null;
        Class fragmentClass;

        switch (menuItem.getItemId()){
            case R.id.nav_font_color:
                fontColorPicker(1);
                break;
            case R.id.nav_font_size:
                fontSizeDialog(1);
                break;
            case R.id.nav_widget_color:
                fontColorPicker(2);
                break;
            case R.id.nav_widget_size:
                fontSizeDialog(2);
                break;
            default:
                break;
        }

        drawerLayout.closeDrawers();
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                selectItemDrawer(item);
                return true;
            }
        });
    }

}
