package com.ngtkn.notesandquotes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RemoteViews;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class SettingsActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    private static final String TAG = "SettingsActivity";
    private final String FONT_COLOR = "FONT_COLOR";
    private final String FONT_SIZE = "FONT_SIZE";
    static String selectedFontColor;
    static int selectedFontSize = 0;
    SharedPreferences sharedPreferences;
    static Spinner colorSpinner;
    static Spinner fontSizeSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        loadSharedPreferences();
        Log.d(TAG, "onCreate: color size : " +selectedFontColor+selectedFontSize);

        // Set up the views
        Button btnSave = findViewById(R.id.btnSaveSettings);
        colorSpinner = findViewById(R.id.spinner_color);
        colorSpinner.setOnItemSelectedListener(this);
        fontSizeSpinner = findViewById(R.id.spinner_font_size);
        fontSizeSpinner.setOnItemSelectedListener(this);
        loadColors();
        loadSizes();
        setColorSpinnerPosition();
        setFontSizeSpinnerPosition();

        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // update the widget with new values
                updateWidget();
                // save values in shared preferences
                if(!selectedFontColor.isEmpty()){
                    sharedPreferences.edit().putString(FONT_COLOR, selectedFontColor).apply();
                }
                if(selectedFontSize != 0){
                    sharedPreferences.edit().putInt(FONT_SIZE, selectedFontSize).apply();
                }
            }
        });
    }

    // Set up on selected listener
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.spinner_color:
                selectedFontColor = parent.getItemAtPosition(position).toString();
                break;
            case R.id.spinner_font_size:
                selectedFontSize = (int) parent.getItemAtPosition(position);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        // TODO: Code if nothing is selected
    }

    // load share preferences
    private void loadSharedPreferences(){
        Log.d(TAG, "loadSharedPreferences: start");
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ngtkn.notesandquotes", Context.MODE_PRIVATE);
        String colorf = sharedPreferences.getString(FONT_COLOR, "Black");
        selectedFontColor = sharedPreferences.getString(FONT_COLOR, "Black");
        int sizef = sharedPreferences.getInt(FONT_SIZE, 14);
        selectedFontSize = sharedPreferences.getInt(FONT_SIZE, 14);

        Log.d(TAG, "loadSharedPreferences: " +colorf + sizef);
    }
    // update widget font color and size
    private void updateWidget(){
        Context context = this;
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        RemoteViews remoteViews = new RemoteViews(context.getPackageName(), R.layout.display_widget);
        ComponentName displayWidget = new ComponentName(context, DisplayWidget.class);

        if(!selectedFontColor.isEmpty()){
            int newColor;
            switch (selectedFontColor){
                case "Black":
                    newColor = Color.BLACK;
                    break;
                case "Red":
                    newColor = Color.RED;
                    break;
                case "White":
                    newColor = Color.WHITE;
                    break;
                case "Green":
                    newColor = Color.GREEN;
                    break;
                case "Gray":
                    newColor = Color.GRAY;
                    break;
                case "Yellow":
                    newColor = Color.YELLOW;
                    break;
                case "Blue":
                    newColor = Color.BLUE;
                    break;
                default:
                    newColor = Color.GRAY;
                    break;
            }
            // set the text color
            remoteViews.setTextColor(R.id.appwidget_text, newColor);
        }
        // set the font size
        if(selectedFontSize != 0){
            remoteViews.setTextViewTextSize(R.id.appwidget_text, TypedValue.COMPLEX_UNIT_SP, selectedFontSize);
        }
        // call the widget manager to update
        appWidgetManager.updateAppWidget(displayWidget, remoteViews);
    }

    // load the available font colors in spinner
    private void loadColors(){
        ArrayList<String> colors = new ArrayList<>();
        colors.add("White");
        colors.add("Black");
        colors.add("Red");
        colors.add("Green");
        colors.add("Gray");
        colors.add("Yellow");
        colors.add("Blue");

        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, colors);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        colorSpinner.setAdapter(arrayAdapter);
    }

    // load the available font sizes in spinner
    private void loadSizes(){
        ArrayList<Integer> fontSizes = new ArrayList<>();
        fontSizes.add(10);
        fontSizes.add(12);
        fontSizes.add(14);
        fontSizes.add(18);
        fontSizes.add(24);
        fontSizes.add(30);
        fontSizes.add(36);

        ArrayAdapter<Integer> arrayAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, fontSizes);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        fontSizeSpinner.setAdapter(arrayAdapter);
    }

    // Set the spinner to the correct position
    private void setColorSpinnerPosition(){
        int len = colorSpinner.getCount();
        for(int i = 0; i < len; i++){
            if(colorSpinner.getItemAtPosition(i).equals(selectedFontColor)){
                colorSpinner.setSelection(i);
                return;
            }
        }
    }

    // Set the spinner to the correct position
    private void setFontSizeSpinnerPosition(){
        Log.d(TAG, "setFontSizeSpinnerPosition: start");
        int len = fontSizeSpinner.getCount();
        for(int i = 0; i < len; i++){
            if(fontSizeSpinner.getItemAtPosition(i).equals(selectedFontSize)){
                fontSizeSpinner.setSelection(i);
            }
        }
    }
}
