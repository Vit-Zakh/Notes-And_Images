package com.sab.notesandimages.adapters;

import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.sab.notesandimages.databinding.NoteLayoutBinding;
import com.sab.notesandimages.models.Note;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class NotesListAdapter extends RecyclerView.Adapter<NotesListAdapter.NoteHolder> {
    private List<Note> notesList = new ArrayList<>();

    @NonNull
    @Override
    public NoteHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        NoteLayoutBinding binding = NoteLayoutBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent, false);
        return new NoteHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteHolder holder, int position) {
        Note currentNote = notesList.get(position);
        holder.binding.titleItemText.setText(currentNote.getTitle());
        holder.binding.descriptionItemText.setText(currentNote.getDescription());
        holder.binding.nicknameItem.setText(currentNote.getNickname());
        Picasso.get()
                .load(currentNote.getImageUrl())
                .fit()
                .into(holder.binding.noteItemImage);
        holder.binding.itemTimestamp.setText((String) DateUtils
                .getRelativeTimeSpanString(currentNote.getTimeAdded().getSeconds() * 1000));
    }

    @Override
    public int getItemCount() {
        return notesList.size();
    }

    public class NoteHolder extends RecyclerView.ViewHolder {
        NoteLayoutBinding binding;

        public NoteHolder(@NonNull NoteLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }

    public void setNotesList(List<Note> notesList) {
        this.notesList = notesList;
        notifyDataSetChanged();
    }
}
