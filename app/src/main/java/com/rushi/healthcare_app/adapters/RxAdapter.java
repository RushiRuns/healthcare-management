package com.rushi.healthcare_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.button.MaterialButton;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rushi.healthcare_app.R;
import com.rushi.healthcare_app.models.PrescriptionRecord;
import java.util.List;

public class RxAdapter extends RecyclerView.Adapter<RxAdapter.RxViewHolder> {

    private List<PrescriptionRecord> prescriptions;
    private OnRxActionListener listener;

    public interface OnRxActionListener {
        void onEdit(PrescriptionRecord record);
        void onDelete(PrescriptionRecord record);
    }

    public RxAdapter(List<PrescriptionRecord> prescriptions, OnRxActionListener listener) {
        this.prescriptions = prescriptions;
        this.listener = listener;
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

        holder.btnEdit.setOnClickListener(v -> {
            if (listener != null) listener.onEdit(record);
        });

        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) listener.onDelete(record);
        });
    }

    @Override
    public int getItemCount() {
        return prescriptions != null ? prescriptions.size() : 0;
    }

    static class RxViewHolder extends RecyclerView.ViewHolder {
        TextView textMedicationName, textDosage, textFrequency, textDate;
        android.widget.ImageView btnEdit, btnDelete;

        public RxViewHolder(@NonNull View itemView) {
            super(itemView);
            textMedicationName = itemView.findViewById(R.id.textMedicationName);
            textDosage = itemView.findViewById(R.id.textDosage);
            textFrequency = itemView.findViewById(R.id.textFrequency);
            textDate = itemView.findViewById(R.id.textDate);
            btnEdit = itemView.findViewById(R.id.btnEditRx);
            btnDelete = itemView.findViewById(R.id.btnDeleteRx);
        }
    }
}