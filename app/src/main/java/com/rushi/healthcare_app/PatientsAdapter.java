package com.rushi.healthcare_app;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.List;

public class PatientsAdapter extends RecyclerView.Adapter<PatientsAdapter.PatientViewHolder> {

    private List<Patient> patientList;
    private List<Patient> patientListFiltered;
    private OnPatientClickListener listener;

    public interface OnPatientClickListener {
        void onPatientClick(Patient patient);
    }

    public PatientsAdapter(List<Patient> patientList, OnPatientClickListener listener) {
        this.patientList = patientList;
        this.patientListFiltered = new ArrayList<>(patientList);
        this.listener = listener;
    }

    @NonNull
    @Override
    public PatientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_patient_card, parent, false);
        return new PatientViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PatientViewHolder holder, int position) {
        Patient patient = patientListFiltered.get(position);
        holder.textPatientName.setText(patient.getName());
        holder.textPatientDetails.setText(patient.getMedicalId() + " • " + patient.getDob());

        if (patient.getName() != null && !patient.getName().isEmpty()) {
            holder.textAvatar.setText(String.valueOf(patient.getName().charAt(0)).toUpperCase());
        }

        holder.itemView.setOnClickListener(v -> listener.onPatientClick(patient));
    }

    @Override
    public int getItemCount() {
        return patientListFiltered.size();
    }

    public void filter(String query) {
        patientListFiltered.clear();
        if (query.isEmpty()) {
            patientListFiltered.addAll(patientList);
        } else {
            String lowerCaseQuery = query.toLowerCase();
            for (Patient patient : patientList) {
                if (patient.getName().toLowerCase().contains(lowerCaseQuery) ||
                        patient.getMedicalId().toLowerCase().contains(lowerCaseQuery)) {
                    patientListFiltered.add(patient);
                }
            }
        }
        notifyDataSetChanged();
    }

    static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView textAvatar, textPatientName, textPatientDetails;

        public PatientViewHolder(@NonNull View itemView) {
            super(itemView);
            textAvatar = itemView.findViewById(R.id.textAvatar);
            textPatientName = itemView.findViewById(R.id.textPatientName);
            textPatientDetails = itemView.findViewById(R.id.textPatientDetails);
        }
    }
}