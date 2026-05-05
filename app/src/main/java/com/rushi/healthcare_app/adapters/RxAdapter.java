package com.rushi.healthcare_app.adapters;
import com.rushi.healthcare_app.models.PrescriptionRecord;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rushi.healthcare_app.R;

import java.util.List;

public class RxAdapter extends RecyclerView.Adapter<RxAdapter.RxViewHolder> {

    private List<PrescriptionRecord> prescriptions;

    public RxAdapter(List<PrescriptionRecord> prescriptions) {
        this.prescriptions = prescriptions;
    }

    @NonNull
    @Override
    public RxViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_rx, parent, false);
        return new RxViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RxViewHolder holder, int position) {
        PrescriptionRecord record = prescriptions.get(position);
        holder.textMedicationName.setText(record.getMedicationName() != null ? record.getMedicationName() : "Unknown");
        holder.textDosage.setText(record.getDosage() != null ? record.getDosage() : "N/A");
        holder.textFrequency.setText(record.getFrequency() != null ? record.getFrequency() : "N/A");
        holder.textDate.setText("Prescribed: " + (record.getStartDate() != null ? record.getStartDate() : "N/A"));
    }

    @Override
    public int getItemCount() {
        return prescriptions != null ? prescriptions.size() : 0;
    }

    static class RxViewHolder extends RecyclerView.ViewHolder {
        TextView textMedicationName, textDosage, textFrequency, textDate;

        public RxViewHolder(@NonNull View itemView) {
            super(itemView);
            textMedicationName = itemView.findViewById(R.id.textMedicationName);
            textDosage = itemView.findViewById(R.id.textDosage);
            textFrequency = itemView.findViewById(R.id.textFrequency);
            textDate = itemView.findViewById(R.id.textDate);
        }
    }
}