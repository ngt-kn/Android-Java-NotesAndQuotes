package com.ngtkn.notesandquotes;

import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

class Notes extends ArrayList<String> {
    static ArrayList<String> notes;

    public Notes() {
        notes = new ArrayList<>();
    }

    public ArrayList<String> getNoteList() {
        return notes;
    }

    void setNotes(ArrayList<String> notes) {
        this.notes = notes;
    }

    public String getNote(int position){
        return notes.get(position);
    }

    void addNewNote(String s){
        notes.add(s);
    }

    void editNote(String s, int position){
        notes.set(position, s);
    }

    void deleteNote(int position){
        notes.remove(position);
    }

    int getSize(){
        return ((notes != null) ? notes.size() : 0);
    }


}
