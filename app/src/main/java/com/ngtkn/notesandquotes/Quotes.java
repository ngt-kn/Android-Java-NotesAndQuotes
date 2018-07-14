package com.ngtkn.notesandquotes;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Random;

class Quotes {
    private static ArrayList<String> quotes;
    private int index = -1;

    Quotes() {
        quotes = new ArrayList<>();
    }

    // Read in quotes from txt file and store in arraylist
    private void loadFromText(Context context){
        try{
            AssetManager asset = context.getAssets();
            InputStream in = asset.open("quotes.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(in);
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            bufferedReader.readLine();
            String line;
            while ((line=bufferedReader.readLine()) != null){
                quotes.add(line);
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
