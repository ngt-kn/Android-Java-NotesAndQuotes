package com.ngtkn.notesandquotes;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
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
    final String FONT_COLOR = "FONT_COLOR";
    final String FONT_SIZE = "FONT_SIZE";
    final String FONT_TYPE = "FONT_TYPE";
    static String selectedFontColor;
    static String selectedFont;
    static int selectedFontSize;
    SharedPreferences sharedPreferences;
    Spinner colorSpinner;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        //sharedPreferences = getApplicationContext().getSharedPreferences("com.ngtkn.notesandquotes", Context.MODE_PRIVATE);
        loadSharedPreferences();
        Button btnSave = findViewById(R.id.btnSaveSettings);
        colorSpinner = findViewById(R.id.spinner_color);
        colorSpinner.setOnItemSelectedListener(this);
        loadColors();
        


        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateWidget();
                if(!selectedFontColor.isEmpty()){
                    sharedPreferences.edit().putString(FONT_COLOR, selectedFontColor);
                }
            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch(parent.getId()){
            case R.id.spinner_color:
                selectedFontColor = parent.getItemAtPosition(position).toString();
                Toast.makeText(this, selectedFontColor, Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void loadSharedPreferences(){
        sharedPreferences = getApplicationContext().getSharedPreferences("com.ngtkn.notesandquotes", Context.MODE_PRIVATE);
//        try {
//            JSONArray jsonArray = new JSONArray(sharedPreferences.getString(ID, "[]"));
//            int len = jsonArray.length();
//            for (int i=0; i < len; i++){
//                notes.addNewNote(jsonArray.getString(i));
//            }
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
    }
    void updateWidget(){
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
            remoteViews.setTextColor(R.id.appwidget_text, newColor);
        }
        appWidgetManager.updateAppWidget(displayWidget, remoteViews);
    }

    void loadColors(){
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
}
