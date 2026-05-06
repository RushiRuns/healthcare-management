package com.rushi.healthcare_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
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

        holder.tvNoteDate.setText(note.consultation_date != null ? note.consultation_date : "N/A");
        holder.tvSymptoms.setText(note.symptoms != null && !note.symptoms.isEmpty() ? note.symptoms : "None recorded");
        holder.tvPlan.setText(note.plan != null && !note.plan.isEmpty() ? note.plan : "None recorded");
        holder.tvDiagnosis.setText(note.diagnosis != null && !note.diagnosis.isEmpty() ? note.diagnosis : "Pending");

        if (note.observations != null && !note.observations.isEmpty()) {
            holder.tvObservations.setText(note.observations);
            holder.tvObservations.setVisibility(View.VISIBLE);
            holder.labelObservations.setVisibility(View.VISIBLE);
        } else {
            holder.tvObservations.setVisibility(View.GONE);
            holder.labelObservations.setVisibility(View.GONE);
        }

        if ("1".equals(note.follow_up_required)) {
            holder.chipFollowUp.setVisibility(View.VISIBLE);
            holder.chipFollowUp.setText("Follow-up: " + note.follow_up_days + " days");
        } else {
            holder.chipFollowUp.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return notesList != null ? notesList.size() : 0;
    }

    static class NotesViewHolder extends RecyclerView.ViewHolder {
        TextView tvNoteDate, tvSymptoms, tvPlan, tvDiagnosis, tvObservations, labelObservations;
        Chip chipFollowUp;

        public NotesViewHolder(@NonNull View itemView) {
            super(itemView);
            tvNoteDate = itemView.findViewById(R.id.tvNoteDate);
            tvSymptoms = itemView.findViewById(R.id.tvSymptoms);
            tvPlan = itemView.findViewById(R.id.tvPlan);
            tvDiagnosis = itemView.findViewById(R.id.tvDiagnosis);
            tvObservations = itemView.findViewById(R.id.tvObservations);
            labelObservations = itemView.findViewById(R.id.labelObservations);
            chipFollowUp = itemView.findViewById(R.id.chipFollowUp);
        }
    }
}