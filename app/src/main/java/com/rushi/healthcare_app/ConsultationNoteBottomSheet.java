package com.rushi.healthcare_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;

public class ConsultationNoteBottomSheet extends BottomSheetDialogFragment {

    private TextInputEditText etSymptoms, etObservations, etDiagnosis, etTreatmentPlan, etFollowUpDays;
    private SwitchMaterial switchFollowUp;
    private MaterialButton btnSaveNote;
    private TextView tvPatientContext;

    public ConsultationNoteBottomSheet() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_consultation_note, container, false);

        tvPatientContext = view.findViewById(R.id.tv_patient_context);
        etSymptoms = view.findViewById(R.id.et_symptoms);
        etObservations = view.findViewById(R.id.et_observations);
        etDiagnosis = view.findViewById(R.id.et_diagnosis);
        etTreatmentPlan = view.findViewById(R.id.et_treatment_plan);
        switchFollowUp = view.findViewById(R.id.switch_follow_up);
        etFollowUpDays = view.findViewById(R.id.et_follow_up_days);
        btnSaveNote = view.findViewById(R.id.btn_save_note);

        switchFollowUp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etFollowUpDays.setEnabled(isChecked);
            if (!isChecked) {
                etFollowUpDays.setText("");
            }
        });

        btnSaveNote.setOnClickListener(v -> {
            String symptoms = etSymptoms.getText() != null ? etSymptoms.getText().toString().trim() : "";
            String observations = etObservations.getText() != null ? etObservations.getText().toString().trim() : "";
            String diagnosis = etDiagnosis.getText() != null ? etDiagnosis.getText().toString().trim() : "";
            String treatment = etTreatmentPlan.getText() != null ? etTreatmentPlan.getText().toString().trim() : "";

            boolean followUpReq = switchFollowUp.isChecked();
            String followUpDays = etFollowUpDays.getText() != null ? etFollowUpDays.getText().toString().trim() : "0";

            if (symptoms.isEmpty() && observations.isEmpty() && diagnosis.isEmpty() && treatment.isEmpty()) {
                android.widget.Toast.makeText(getContext(), "Note cannot be entirely empty", android.widget.Toast.LENGTH_SHORT).show();
                return;
            }

            // Get the patient ID that was passed when opening the bottom sheet
            String patientId = "";
            if (getArguments() != null) {
                patientId = getArguments().getString("PATIENT_ID", "");
            }

            java.util.HashMap<String, Object> noteData = new java.util.HashMap<>();
            noteData.put("patient_id", patientId);
            // Hardcoding doctor_id to 1 for now assuming single user. Update if you have an Auth/Session manager
            noteData.put("doctor_id", 1);
            noteData.put("symptoms", symptoms);
            noteData.put("observations", observations);
            noteData.put("diagnosis", diagnosis);
            noteData.put("treatment_plan", treatment);
            noteData.put("follow_up_required", followUpReq ? 1 : 0);
            noteData.put("follow_up_days", followUpDays.isEmpty() ? 0 : Integer.parseInt(followUpDays));

            ApiService apiService = RetrofitClient.getApiService();
            apiService.addNote(noteData).enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        android.widget.Toast.makeText(getContext(), "Note saved successfully", android.widget.Toast.LENGTH_SHORT).show();
                        dismiss();
                        // Optionally, refresh the list in the Fragment here
                    } else {
                        android.widget.Toast.makeText(getContext(), "Failed to save note", android.widget.Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(retrofit2.Call<Void> call, Throwable t) {
                    android.widget.Toast.makeText(getContext(), "Error: " + t.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
                }
            });
        });

        return view;
    }
}
