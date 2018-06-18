package com.ngtkn.notesandquotes;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

class Notes {
    ArrayList<String> notes = new ArrayList<>();

    public Notes() {
    }

    public ArrayList<String> getNotes() {
        return notes;
    }

    public String getNote(int position){
        return notes.get(position);
    }

    void addNewNote(String s){
        //TODO: method to add a new note to the array list
        notes.add(s);
    }

    void editNote(){
        //TODO: method to edit existing note
    }

    void deleteNote(int position){
        //TODO: method to delete note from array
        notes.remove(position);
    }

    public void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }
}
