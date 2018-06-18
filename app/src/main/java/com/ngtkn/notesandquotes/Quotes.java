package com.ngtkn.notesandquotes;

import android.util.Log;

import java.util.ArrayList;
import java.util.Random;

class Quotes {
    private static final String TAG = "Quotes";
    ArrayList<String> quotes;
    private int index = -1;

    public Quotes() {
        this.quotes = new ArrayList<>();
    }

    void loadFromText(){
        //TODO: Read in quotes from string into array
        quotes.add("1");
        quotes.add("2");
        quotes.add("3");
        quotes.add("4");
        quotes.add("5");
        quotes.add("6");
    }

    String getNewQuote(){
        Random rand = new Random();
        
        if(quotes.size() == 0){
            Log.d(TAG, "getNewQuote: load text");
            // Load quotes from text file, return random quote
            loadFromText();
            index = rand.nextInt(quotes.size()-1);
            return quotes.get(index);
        } else {
            Log.d(TAG, "getNewQuote: already loaded");
            // quotes already loaded, return new random quote
            int currentIndex = index;
            index = rand.nextInt(quotes.size()-1);
            while(index == currentIndex ){
                index = rand.nextInt(quotes.size()-1);
            }
            return quotes.get(index);
        }
    }
}
