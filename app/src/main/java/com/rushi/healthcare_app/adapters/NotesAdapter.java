package com.rushi.healthcare_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rushi.healthcare_app.R;
import com.rushi.healthcare_app.models.ConsultationNote;
import java.util.List;

public class NotesAdapter extends RecyclerView.Adapter<NotesAdapter.NotesViewHolder> {
    private List<ConsultationNote> notesList;

    public NotesAdapter(List<ConsultationNote> notesList) {
        this.notesList = notesList;
    }

    @NonNull
    @Override
    public NotesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_note, parent, false);
        return new NotesViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NotesViewHolder holder, int position) {
        ConsultationNote note = notesList.get(position);
        holder.tvNoteDate.setText(note.consultation_date);
        holder.tvSymptoms.setText(note.symptoms);
        holder.tvPlan.setText(note.plan);
    }

    @Override
    public int getItemCount() {
        return notesList != null ? notesList.size() : 0;
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoteDate, tvSymptoms, tvPlan;
        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoteDate = itemView.findViewById(R.id.tvNoteDate);
            tvSymptoms = itemView.findViewById(R.id.tvSymptoms);
            tvPlan = itemView.findViewById(R.id.tvPlan);
        }
    }
}