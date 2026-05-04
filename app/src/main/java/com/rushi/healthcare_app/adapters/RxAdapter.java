package com.rushi.healthcare_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rushi.healthcare_app.R;
import com.rushi.healthcare_app.models.Prescription;
import java.util.List;

public class RxAdapter extends RecyclerView.Adapter<RxAdapter.RxViewHolder> {
    private List<Prescription> prescriptions;

    public RxAdapter(List<Prescription> prescriptions) {
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
        Prescription rx = prescriptions.get(position);
        holder.tvMedicineName.setText(rx.medication_name);
        holder.tvDosage.setText(rx.dosage);
        holder.tvInstructions.setText(rx.instructions);
    }

    @Override
    public int getItemCount() {
        return prescriptions != null ? prescriptions.size() : 0;
    }

    static class RxViewHolder extends RecyclerView.ViewHolder {
        TextView tvMedicineName, tvDosage, tvInstructions;
        public RxViewHolder(@NonNull View itemView) {
            super(itemView);
            tvMedicineName = itemView.findViewById(R.id.tvMedicineName);
            tvDosage = itemView.findViewById(R.id.tvDosage);
            tvInstructions = itemView.findViewById(R.id.tvInstructions);
        }
    }
}