package com.example.medianotesapp;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NoteViewHolder> {
    private List<Note> notes;
    private Context context; // We need this to open new screens and show Toasts

    // Updated Constructor
    public NotesAdapter(Context context, List<Note> notes) {
        this.context = context;
        this.notes = notes;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_note, parent, false);
        return new NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        Note note = notes.get(position);
        holder.title.setText(note.title);
        holder.date.setText(note.date);

        // Roll 6 Customization
        if (note.reminderFlag == 1) {
            holder.badge.setText("Review");
            holder.badge.setVisibility(View.VISIBLE);
        } else {
            holder.badge.setVisibility(View.GONE);
        }

        // Image Loading
        if (note.imagePath != null && !note.imagePath.isEmpty()) {
            if (note.imagePath.equals("CAMERA_THUMBNAIL_ATTACHED")) {
                holder.image.setImageResource(android.R.drawable.ic_menu_camera);
            } else {
                Glide.with(context)
                        .load(Uri.parse(note.imagePath))
                        .centerCrop()
                        .into(holder.image);
            }
        } else {
            holder.image.setBackgroundColor(android.graphics.Color.parseColor("#EEEEEE"));
        }

        // --- DELETE LOGIC ---
        holder.btnDelete.setOnClickListener(v -> {
            DatabaseHelper db = new DatabaseHelper(context);
            db.deleteNote(note.id); // Delete from DB
            notes.remove(position); // Remove from our current list
            notifyItemRemoved(position); // Visually animate the removal
            notifyItemRangeChanged(position, notes.size()); // Update grid math
            Toast.makeText(context, "Note Deleted", Toast.LENGTH_SHORT).show();
        });

        // --- EDIT LOGIC ---
        holder.btnEdit.setOnClickListener(v -> {
            Intent intent = new Intent(context, AddNoteActivity.class);
            intent.putExtra("isEditMode", true);
            intent.putExtra("note_id", note.id);
            intent.putExtra("title", note.title);
            intent.putExtra("desc", note.description);
            intent.putExtra("image_path", note.imagePath);
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return notes.size();
    }

    static class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView title, date, badge;
        ImageView image;
        ImageButton btnEdit, btnDelete; // <--- This is what was missing!

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.noteTitle);
            date = itemView.findViewById(R.id.noteDate);
            badge = itemView.findViewById(R.id.badgeReview);
            image = itemView.findViewById(R.id.noteImage);

            // <--- We have to link the buttons to the XML here! --->
            btnEdit = itemView.findViewById(R.id.btnEdit);
            btnDelete = itemView.findViewById(R.id.btnDelete);
        }
    }
}