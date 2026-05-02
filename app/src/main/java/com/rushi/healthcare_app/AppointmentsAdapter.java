package com.rushi.healthcare_app;

import android.content.res.ColorStateList;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.chip.Chip;
import java.util.List;

public class AppointmentsAdapter extends RecyclerView.Adapter<AppointmentsAdapter.ViewHolder> {

    private List<Appointment> appointmentList;
    private OnAppointmentClickListener listener;

    public interface OnAppointmentClickListener {
        void onAppointmentClick(Appointment appointment);
    }

    public AppointmentsAdapter(List<Appointment> appointmentList, OnAppointmentClickListener listener) {
        this.appointmentList = appointmentList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_appointment_card, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Appointment appt = appointmentList.get(position);

        String timeDisplay = appt.getTime();
        if (timeDisplay != null && timeDisplay.length() > 10) {
            timeDisplay = timeDisplay.substring(11, 16);
        }
        holder.textTime.setText(timeDisplay);

        holder.textPatientName.setText(appt.getPatientName());

        String status = appt.getStatus();
        if (status == null || status.trim().isEmpty()) {
            status = "scheduled";
        }

        String displayStatus = status.substring(0, 1).toUpperCase() + status.substring(1).toLowerCase();
        holder.chipStatus.setText(displayStatus);

        int bgColor = R.color.status_wait_bg;
        int textColor = R.color.status_wait_text;

        switch (status.toLowerCase()) {
            case "completed":
                bgColor = R.color.status_done_bg;
                textColor = R.color.status_done_text;
                break;
            case "cancelled":
                bgColor = R.color.status_cancel_bg;
                textColor = R.color.status_cancel_text;
                break;
            default:
                bgColor = R.color.status_wait_bg;
                textColor = R.color.status_wait_text;
                break;
        }

        holder.chipStatus.setChipBackgroundColor(ColorStateList.valueOf(ContextCompat.getColor(holder.itemView.getContext(), bgColor)));
        holder.chipStatus.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), textColor));

        holder.itemView.setOnClickListener(v -> listener.onAppointmentClick(appt));
    }

    @Override
    public int getItemCount() {
        return appointmentList == null ? 0 : appointmentList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textTime;
        TextView textPatientName;
        Chip chipStatus;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textTime = itemView.findViewById(R.id.textTime);
            textPatientName = itemView.findViewById(R.id.textPatientName);
            chipStatus = itemView.findViewById(R.id.chipStatus);
        }
    }
}