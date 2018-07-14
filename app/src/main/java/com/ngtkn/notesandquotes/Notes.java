package com.ngtkn.notesandquotes;


import java.util.ArrayList;

class Notes extends ArrayList<String> {
    private static ArrayList<String> notes;

    Notes() {
        notes = new ArrayList<>();
    }

    public ArrayList<String> getNoteList() {
        return notes;
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
