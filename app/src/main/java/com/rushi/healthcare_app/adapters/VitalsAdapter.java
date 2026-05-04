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

    public VitalsAdapter(List<VitalSign> vitalsList) {
        this.vitalsList = vitalsList;
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
        holder.tvVitalsBp.setText(vitals.blood_pressure);
        holder.tvVitalsPulse.setText(vitals.heart_rate + " bpm");
        holder.tvVitalsTemp.setText(vitals.temperature);
        holder.tvVitalsWeight.setText(vitals.weight + " kg");
    }

    @Override
    public int getItemCount() {
        return vitalsList != null ? vitalsList.size() : 0;
    }

    static class VitalsViewHolder extends RecyclerView.ViewHolder {
        TextView tvVitalsDate, tvVitalsBp, tvVitalsPulse, tvVitalsTemp, tvVitalsWeight;
        public VitalsViewHolder(@NonNull View itemView) {
            super(itemView);
            tvVitalsDate = itemView.findViewById(R.id.tvVitalsDate);
            tvVitalsBp = itemView.findViewById(R.id.tvVitalsBp);
            tvVitalsPulse = itemView.findViewById(R.id.tvVitalsPulse);
            tvVitalsTemp = itemView.findViewById(R.id.tvVitalsTemp);
            tvVitalsWeight = itemView.findViewById(R.id.tvVitalsWeight);
        }
    }
}