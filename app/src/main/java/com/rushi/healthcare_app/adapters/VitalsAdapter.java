package com.rushi.healthcare_app.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.rushi.healthcare_app.R;
import com.rushi.healthcare_app.models.VitalSign;
import java.util.List;

public class VitalsAdapter extends RecyclerView.Adapter<VitalsAdapter.VitalsViewHolder> {
    private List<VitalSign> vitalsList;
    private OnVitalActionListener listener;

    public interface OnVitalActionListener {
        void onEdit(VitalSign vital);
        void onDelete(VitalSign vital);
    }

    public VitalsAdapter(List<VitalSign> vitalsList, OnVitalActionListener listener) {
        this.vitalsList = vitalsList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VitalsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_vitals, parent, false);
        return new VitalsViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VitalsViewHolder holder, int position) {
        VitalSign vitals = vitalsList.get(position);
        holder.tvVitalsDate.setText("Date: " + vitals.recorded_at);

        holder.tvVitalsBp.setText((vitals.blood_pressure != null && !vitals.blood_pressure.equals("/")) ? vitals.blood_pressure : "-");
        holder.tvVitalsPulse.setText((vitals.heart_rate != null) ? vitals.heart_rate + " bpm" : "-");
        holder.tvVitalsTemp.setText((vitals.temperature != null) ? vitals.temperature : "-");
        holder.tvVitalsWeight.setText((vitals.weight != null) ? vitals.weight + " kg" : "-");

        holder.btnEditVital.setOnClickListener(v -> listener.onEdit(vitals));
        holder.btnDeleteVital.setOnClickListener(v -> listener.onDelete(vitals));
    }

    @Override
    public int getItemCount() {
        return vitalsList != null ? vitalsList.size() : 0;
    }

    static class VitalsViewHolder extends RecyclerView.ViewHolder {
        TextView tvVitalsDate, tvVitalsBp, tvVitalsPulse, tvVitalsTemp, tvVitalsWeight;
        View btnEditVital, btnDeleteVital;

        public VitalsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVitalsDate = itemView.findViewById(R.id.tvVitalsDate);
            tvVitalsBp = itemView.findViewById(R.id.tvVitalsBp);
            tvVitalsPulse = itemView.findViewById(R.id.tvVitalsPulse);
            tvVitalsTemp = itemView.findViewById(R.id.tvVitalsTemp);
            tvVitalsWeight = itemView.findViewById(R.id.tvVitalsWeight);
            btnEditVital = itemView.findViewById(R.id.btnEditVital);
            btnDeleteVital = itemView.findViewById(R.id.btnDeleteVital);
        }
    }
}