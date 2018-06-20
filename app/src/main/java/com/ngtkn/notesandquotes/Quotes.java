package com.ngtkn.notesandquotes;

import android.content.Context;
import android.content.res.AssetManager;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

class Quotes {
    private static final String TAG = "Quotes";
    static ArrayList<String> quotes;
    private int index = -1;

    public Quotes() {
        this.quotes = new ArrayList<>();
    }

    // Read in quotes from txt file and store in arraylist
    void loadFromText(Context context){
        try{
            AssetManager asset = context.getAssets();
            InputStream in = asset.open("test.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            String line = bufferedReader.readLine();
            while ((line=bufferedReader.readLine()) != null){
                quotes.add(line);
                Log.d(TAG, "loadFromText: " + line);
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    // Return a new random quote
    String getNewQuote(Context context){
        Random rand = new Random();
        
        if(quotes.size() == 0){
            // Load quotes from text file, return random quote
            loadFromText(context);
            index = rand.nextInt(quotes.size()-1);
            return quotes.get(index);
        } else {
            // quotes already loaded, return new random quote
            int currentIndex = index;
            index = rand.nextInt(quotes.size());
            while(index == currentIndex ){
                index = rand.nextInt(quotes.size());
            }
            return quotes.get(index);
        }
    }
}
