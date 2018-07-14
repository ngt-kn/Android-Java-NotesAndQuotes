package com.ngtkn.notesandquotes;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.NotesViewHolder> {
    private ArrayList<String> notes;
    private Context context;
    int dynamicTextSize;
    int dynamicTextColor;

    RecyclerViewAdapter(Context context, ArrayList<String> notes) {
        this.notes = notes;
        this.context = context;
        this.dynamicTextSize = 14;
        this.dynamicTextColor = Color.parseColor("#000000");
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Called by the layout manager when it needs a new view
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_layout, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        // Called by recycler view for new data to be stored in a view holder, to display
        holder.noteView.setText(notes.get(position));
        holder.noteView.setTextSize(dynamicTextSize);
        holder.noteView.setTextColor(dynamicTextColor);

    }

    @Override
    public int getItemCount() {
        return ((notes != null) && (notes.size() != 0) ? notes.size() : 0);
    }

    void loadNewData(ArrayList<String> newNotes) {
        notes = newNotes;
        notifyDataSetChanged();
    }


    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView noteView = null;

        NotesViewHolder(View itemView) {
            super(itemView);
            this.noteView = itemView.findViewById(R.id.notes_text_view);
        }
    }

}
