package com.rushi.healthcare_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
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
    private LinearLayout llFollowUpStatus;
    private RadioGroup rgFollowUpStatus;
    private Runnable onSuccessCallback;

    public ConsultationNoteBottomSheet() { }

    public void setOnSuccessCallback(Runnable callback) {
        this.onSuccessCallback = callback;
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
        llFollowUpStatus = view.findViewById(R.id.ll_follow_up_status);
        rgFollowUpStatus = view.findViewById(R.id.rg_follow_up_status);

        switchFollowUp.setOnCheckedChangeListener((buttonView, isChecked) -> {
            etFollowUpDays.setEnabled(isChecked);
            llFollowUpStatus.setVisibility(isChecked ? View.VISIBLE : View.GONE);
            if (!isChecked) {
                etFollowUpDays.setText("");
            }
        });

        String noteId = getArguments() != null ? getArguments().getString("NOTE_ID", null) : null;

        if (noteId != null) {
            btnSaveNote.setText("Update Note");
            tvPatientContext.setText("Edit Consultation Note");
            etSymptoms.setText(getArguments().getString("SYMPTOMS", ""));
            etObservations.setText(getArguments().getString("OBSERVATIONS", ""));
            etDiagnosis.setText(getArguments().getString("DIAGNOSIS", ""));
            etTreatmentPlan.setText(getArguments().getString("PLAN", ""));

            String followUpReq = getArguments().getString("FOLLOW_UP_REQ", "0");
            if ("1".equals(followUpReq)) {
                switchFollowUp.setChecked(true);
                etFollowUpDays.setEnabled(true);
                etFollowUpDays.setText(getArguments().getString("FOLLOW_UP_DAYS", ""));
                llFollowUpStatus.setVisibility(View.VISIBLE);

                // Set the correct radio button
                String status = getArguments().getString("FOLLOW_UP_STATUS", "pending");
                if ("completed".equalsIgnoreCase(status)) {
                    rgFollowUpStatus.check(R.id.rb_completed);
                } else if ("cancelled".equalsIgnoreCase(status)) {
                    rgFollowUpStatus.check(R.id.rb_cancelled);
                } else {
                    rgFollowUpStatus.check(R.id.rb_pending);
                }
            }
        }

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

            String status = "pending";
            if (followUpReq) {
                int selectedId = rgFollowUpStatus.getCheckedRadioButtonId();
                if (selectedId == R.id.rb_completed) status = "completed";
                else if (selectedId == R.id.rb_cancelled) status = "cancelled";
            }

            java.util.HashMap<String, Object> noteData = new java.util.HashMap<>();
            noteData.put("symptoms", symptoms);
            noteData.put("observations", observations);
            noteData.put("diagnosis", diagnosis);
            noteData.put("treatment_plan", treatment);
            noteData.put("follow_up_required", followUpReq ? 1 : 0);
            noteData.put("follow_up_days", followUpDays.isEmpty() ? 0 : Integer.parseInt(followUpDays));
            noteData.put("follow_up_status", status);
            noteData.put("patient_id", getArguments() != null ? getArguments().getString("PATIENT_ID", "") : "");

            ApiService apiService = RetrofitClient.getApiService();
            retrofit2.Call<Void> call;

            if (noteId != null) {
                noteData.put("note_id", noteId);
                call = apiService.updateNote(noteData);
            } else {
                noteData.put("doctor_id", 1);
                call = apiService.addNote(noteData);
            }

            call.enqueue(new retrofit2.Callback<Void>() {
                @Override
                public void onResponse(retrofit2.Call<Void> call, retrofit2.Response<Void> response) {
                    if (response.isSuccessful()) {
                        android.widget.Toast.makeText(getContext(), noteId != null ? "Note updated" : "Note saved", android.widget.Toast.LENGTH_SHORT).show();
                        if (onSuccessCallback != null) onSuccessCallback.run();
                        dismiss();
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