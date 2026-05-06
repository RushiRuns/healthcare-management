package com.rushi.healthcare_app;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import android.util.Log;
import java.util.List;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.rushi.healthcare_app.adapters.NotesAdapter;
import com.rushi.healthcare_app.adapters.RxAdapter;
import com.rushi.healthcare_app.adapters.VitalsAdapter;
import com.rushi.healthcare_app.models.ConsultationNoteResponse;
import com.rushi.healthcare_app.models.PrescriptionResponse;
import com.rushi.healthcare_app.models.VitalSignResponse;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientFragments {

    public static class OverviewFragment extends Fragment {
        private String patientId;
        private RecyclerView recyclerViewActive;
        private RecyclerView recyclerViewPast;
        private com.rushi.healthcare_app.adapters.HistoryAdapter activeAdapter;
        private com.rushi.healthcare_app.adapters.HistoryAdapter pastAdapter;
        private android.widget.Button btnAddConditionTop;

        public static OverviewFragment newInstance(String patientId) {
            OverviewFragment fragment = new OverviewFragment();
            Bundle args = new Bundle();
            args.putString("PATIENT_ID", patientId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                patientId = getArguments().getString("PATIENT_ID");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_overview, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            recyclerViewActive = view.findViewById(R.id.recyclerViewActiveConditions);
            recyclerViewActive.setLayoutManager(new LinearLayoutManager(getContext()));

            recyclerViewPast = view.findViewById(R.id.recyclerViewPastHistory);
            recyclerViewPast.setLayoutManager(new LinearLayoutManager(getContext()));

            btnAddConditionTop = view.findViewById(R.id.btnAddConditionTop);
            btnAddConditionTop.setOnClickListener(v -> showAddConditionBottomSheet(null));

            fetchOverviewData();
        }

        private void showAddConditionBottomSheet(@Nullable MedicalHistory existingRecord) {
            com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                    new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());

            View bottomSheetView = LayoutInflater.from(getContext())
                    .inflate(R.layout.bottom_sheet_add_condition, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            android.widget.EditText inputName = bottomSheetView.findViewById(R.id.inputConditionName);
            android.widget.EditText inputDate = bottomSheetView.findViewById(R.id.inputDiagnosisDate);
            android.widget.RadioGroup radioGroup = bottomSheetView.findViewById(R.id.radioGroupStatus);
            android.widget.Button btnSave = bottomSheetView.findViewById(R.id.btnSaveCondition);

            if (existingRecord != null) {
                inputName.setText(existingRecord.getConditionName());
                inputDate.setText(existingRecord.getDiagnosisDate());
                if (existingRecord.getStatus() != null && existingRecord.getStatus().equalsIgnoreCase("Resolved")) {
                    ((android.widget.RadioButton)bottomSheetView.findViewById(radioGroup.getChildAt(1).getId())).setChecked(true);
                } else {
                    ((android.widget.RadioButton)bottomSheetView.findViewById(radioGroup.getChildAt(0).getId())).setChecked(true);
                }
                btnSave.setText("Update Condition");
            }

            inputDate.setOnClickListener(v -> {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                new android.app.DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    inputDate.setText(selectedDate);
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
            });

            btnSave.setOnClickListener(v -> {
                String conditionName = inputName.getText().toString().trim();
                String diagnosisDate = inputDate.getText().toString().trim();

                int selectedId = radioGroup.getCheckedRadioButtonId();
                android.widget.RadioButton selectedRadio = bottomSheetView.findViewById(selectedId);
                String status = selectedRadio.getText().toString();

                if (conditionName.isEmpty()) {
                    Toast.makeText(getContext(), "Please enter a condition name", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (existingRecord == null) {
                    saveConditionToDatabase(conditionName, diagnosisDate, status, bottomSheetDialog);
                } else {
                    updateConditionInDatabase(existingRecord.getHistoryId(), conditionName, diagnosisDate, status, bottomSheetDialog);
                }
            });

            bottomSheetDialog.show();
        }

        private void saveConditionToDatabase(String name, String date, String status, com.google.android.material.bottomsheet.BottomSheetDialog dialog) {
            java.util.HashMap<String, String> data = new java.util.HashMap<>();
            data.put("patient_id", patientId);
            data.put("condition_name", name);
            data.put("diagnosis_date", date);
            data.put("status", status);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.addMedicalCondition(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Condition added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        fetchOverviewData();
                    } else {
                        Toast.makeText(getContext(), "Failed to add", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updateConditionInDatabase(String historyId, String name, String date, String status, com.google.android.material.bottomsheet.BottomSheetDialog dialog) {
            java.util.HashMap<String, String> data = new java.util.HashMap<>();
            data.put("history_id", historyId);
            data.put("condition_name", name);
            data.put("diagnosis_date", date);
            data.put("status", status);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.updateMedicalCondition(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Condition updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        fetchOverviewData();
                    } else {
                        Toast.makeText(getContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void deleteConditionFromDatabase(String historyId) {
            java.util.HashMap<String, String> data = new java.util.HashMap<>();
            data.put("history_id", historyId);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.deleteMedicalCondition(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Condition deleted", Toast.LENGTH_SHORT).show();
                        fetchOverviewData();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void fetchOverviewData() {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getPatientDetails(patientId).enqueue(new Callback<PatientResponse>() {
                @Override
                public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().getData() != null) {
                        List<MedicalHistory> history = response.body().getData().getMedicalHistory();
                        if (history != null) {
                            java.util.List<MedicalHistory> activeList = new java.util.ArrayList<>();
                            java.util.List<MedicalHistory> pastList = new java.util.ArrayList<>();

                            for (MedicalHistory item : history) {
                                if (item.getStatus() != null && item.getStatus().equalsIgnoreCase("Active")) {
                                    activeList.add(item);
                                } else {
                                    pastList.add(item);
                                }
                            }

                            com.rushi.healthcare_app.adapters.HistoryAdapter.OnHistoryActionListener listener = new com.rushi.healthcare_app.adapters.HistoryAdapter.OnHistoryActionListener() {
                                @Override
                                public void onEdit(MedicalHistory item) {
                                    showAddConditionBottomSheet(item);
                                }
                                @Override
                                public void onDelete(MedicalHistory item) {
                                    new android.app.AlertDialog.Builder(getContext())
                                            .setTitle("Delete Condition")
                                            .setMessage("Are you sure you want to delete this record?")
                                            .setPositiveButton("Delete", (dialog, which) -> deleteConditionFromDatabase(item.getHistoryId()))
                                            .setNegativeButton("Cancel", null)
                                            .show();
                                }
                            };

                            activeAdapter = new com.rushi.healthcare_app.adapters.HistoryAdapter(activeList, listener);
                            recyclerViewActive.setAdapter(activeAdapter);

                            pastAdapter = new com.rushi.healthcare_app.adapters.HistoryAdapter(pastList, listener);
                            recyclerViewPast.setAdapter(pastAdapter);
                        }
                    }
                }
                @Override
                public void onFailure(Call<PatientResponse> call, Throwable t) {
                    android.util.Log.e("OverviewFragment", "Overview fetch failed", t);
                }
            });
        }
    }

    public static class RxFragment extends Fragment {
        private String patientId;
        private RecyclerView recyclerViewActiveRx;
        private RecyclerView recyclerViewPastRx;
        private RxAdapter activeAdapter;
        private RxAdapter pastAdapter;
        private android.widget.Button btnAddMedication;

        public static RxFragment newInstance(String patientId) {
            RxFragment fragment = new RxFragment();
            Bundle args = new Bundle();
            args.putString("PATIENT_ID", patientId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                patientId = getArguments().getString("PATIENT_ID");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_rx, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);

            recyclerViewActiveRx = view.findViewById(R.id.recyclerViewActiveRx);
            recyclerViewActiveRx.setLayoutManager(new LinearLayoutManager(getContext()));

            recyclerViewPastRx = view.findViewById(R.id.recyclerViewPastRx);
            recyclerViewPastRx.setLayoutManager(new LinearLayoutManager(getContext()));

            btnAddMedication = view.findViewById(R.id.btnAddMedication);
            btnAddMedication.setOnClickListener(v -> showAddPrescriptionBottomSheet(null));

            fetchPrescriptions();
            fetchPastPrescriptions();
        }

        private void showAddPrescriptionBottomSheet(@Nullable com.rushi.healthcare_app.models.PrescriptionRecord existingRecord) {
            com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                    new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());

            View bottomSheetView = LayoutInflater.from(getContext())
                    .inflate(R.layout.bottom_sheet_add_prescription, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            android.widget.TextView sheetTitle = bottomSheetView.findViewById(R.id.textRxSheetTitle);
            android.widget.EditText inputName = bottomSheetView.findViewById(R.id.inputMedicationName);
            android.widget.EditText inputDosage = bottomSheetView.findViewById(R.id.inputDosage);
            android.widget.EditText inputFrequency = bottomSheetView.findViewById(R.id.inputFrequency);
            android.widget.EditText inputDate = bottomSheetView.findViewById(R.id.inputStartDate);
            android.widget.RadioGroup radioGroup = bottomSheetView.findViewById(R.id.radioGroupRxStatus);
            android.widget.Button btnSave = bottomSheetView.findViewById(R.id.btnSavePrescription);

            if (existingRecord != null) {
                sheetTitle.setText("Update Medication");
                inputName.setText(existingRecord.getMedicationName());
                inputDosage.setText(existingRecord.getDosage());
                inputFrequency.setText(existingRecord.getFrequency());
                inputDate.setText(existingRecord.getStartDate());

                if (existingRecord.getStatus() != null && existingRecord.getStatus().equalsIgnoreCase("Completed")) {
                    ((android.widget.RadioButton)bottomSheetView.findViewById(R.id.radioRxCompleted)).setChecked(true);
                } else {
                    ((android.widget.RadioButton)bottomSheetView.findViewById(R.id.radioRxActive)).setChecked(true);
                }
                btnSave.setText("Update Prescription");
            }

            inputDate.setOnClickListener(v -> {
                java.util.Calendar calendar = java.util.Calendar.getInstance();
                new android.app.DatePickerDialog(getContext(), (view1, year, month, dayOfMonth) -> {
                    String selectedDate = String.format(java.util.Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                    inputDate.setText(selectedDate);
                }, calendar.get(java.util.Calendar.YEAR), calendar.get(java.util.Calendar.MONTH), calendar.get(java.util.Calendar.DAY_OF_MONTH)).show();
            });

            btnSave.setOnClickListener(v -> {
                String name = inputName.getText().toString().trim();
                String dosage = inputDosage.getText().toString().trim();
                String freq = inputFrequency.getText().toString().trim();
                String date = inputDate.getText().toString().trim();

                int selectedId = radioGroup.getCheckedRadioButtonId();
                android.widget.RadioButton selectedRadio = bottomSheetView.findViewById(selectedId);
                String status = selectedRadio.getText().toString();

                if (name.isEmpty() || dosage.isEmpty() || date.isEmpty()) {
                    Toast.makeText(getContext(), "Please fill in Name, Dosage, and Start Date", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (existingRecord == null) {
                    savePrescriptionToDatabase(name, dosage, freq, date, status, bottomSheetDialog);
                } else {
                    updatePrescriptionInDatabase(existingRecord.getPrescriptionId(), name, dosage, freq, date, status, bottomSheetDialog);
                }
            });

            bottomSheetDialog.show();
        }

        private void savePrescriptionToDatabase(String name, String dosage, String freq, String date, String status, com.google.android.material.bottomsheet.BottomSheetDialog dialog) {
            java.util.HashMap<String, String> data = new java.util.HashMap<>();
            data.put("patient_id", patientId);
            data.put("medication_name", name);
            data.put("dosage", dosage);
            data.put("frequency", freq);
            data.put("start_date", date);
            data.put("status", status);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.addPrescription(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Prescription added", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        fetchPrescriptions();
                        fetchPastPrescriptions();
                    } else {
                        Toast.makeText(getContext(), "Failed to add prescription", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void updatePrescriptionInDatabase(String prescriptionId, String name, String dosage, String freq, String date, String status, com.google.android.material.bottomsheet.BottomSheetDialog dialog) {
            java.util.HashMap<String, String> data = new java.util.HashMap<>();
            data.put("prescription_id", prescriptionId);
            data.put("medication_name", name);
            data.put("dosage", dosage);
            data.put("frequency", freq);
            data.put("start_date", date);
            data.put("status", status);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.updatePrescription(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Prescription updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        fetchPrescriptions();
                        fetchPastPrescriptions();
                    } else {
                        Toast.makeText(getContext(), "Failed to update prescription", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void deletePrescriptionFromDatabase(String prescriptionId) {
            java.util.HashMap<String, String> data = new java.util.HashMap<>();
            data.put("prescription_id", prescriptionId);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.deletePrescription(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Prescription deleted", Toast.LENGTH_SHORT).show();
                        fetchPrescriptions();
                        fetchPastPrescriptions();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete prescription", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void fetchPrescriptions() {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getPrescriptions(patientId).enqueue(new Callback<PrescriptionResponse>() {
                @Override
                public void onResponse(Call<PrescriptionResponse> call, Response<PrescriptionResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().records != null) {
                        activeAdapter = new RxAdapter(response.body().records, new RxAdapter.OnRxActionListener() {
                            @Override
                            public void onEdit(com.rushi.healthcare_app.models.PrescriptionRecord record) {
                                showAddPrescriptionBottomSheet(record);
                            }
                            @Override
                            public void onDelete(com.rushi.healthcare_app.models.PrescriptionRecord record) {
                                new android.app.AlertDialog.Builder(getContext())
                                        .setTitle("Delete Prescription")
                                        .setMessage("Are you sure you want to delete this prescription?")
                                        .setPositiveButton("Delete", (dialog, which) -> deletePrescriptionFromDatabase(record.getPrescriptionId()))
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        });
                        recyclerViewActiveRx.setAdapter(activeAdapter);
                    }
                }
                @Override
                public void onFailure(Call<PrescriptionResponse> call, Throwable t) {
                    android.util.Log.e("RxError", "Fetch Active Error: " + t.getMessage());
                }
            });
        }

        private void fetchPastPrescriptions() {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getPastPrescriptions(patientId).enqueue(new Callback<PrescriptionResponse>() {
                @Override
                public void onResponse(Call<PrescriptionResponse> call, Response<PrescriptionResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().records != null) {
                        pastAdapter = new RxAdapter(response.body().records, new RxAdapter.OnRxActionListener() {
                            @Override
                            public void onEdit(com.rushi.healthcare_app.models.PrescriptionRecord record) {
                                showAddPrescriptionBottomSheet(record);
                            }
                            @Override
                            public void onDelete(com.rushi.healthcare_app.models.PrescriptionRecord record) {
                                new android.app.AlertDialog.Builder(getContext())
                                        .setTitle("Delete Prescription")
                                        .setMessage("Are you sure you want to delete this prescription?")
                                        .setPositiveButton("Delete", (dialog, which) -> deletePrescriptionFromDatabase(record.getPrescriptionId()))
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        });
                        recyclerViewPastRx.setAdapter(pastAdapter);
                    }
                }
                @Override
                public void onFailure(Call<PrescriptionResponse> call, Throwable t) {
                    android.util.Log.e("RxError", "Fetch Past Error: " + t.getMessage());
                }
            });
        }
    }

    public static class NotesFragment extends Fragment {
        private String patientId;
        private RecyclerView recyclerView;
        private NotesAdapter adapter;

        public static NotesFragment newInstance(String patientId) {
            NotesFragment fragment = new NotesFragment();
            Bundle args = new Bundle();
            args.putString("PATIENT_ID", patientId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                patientId = getArguments().getString("PATIENT_ID");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_notes, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d("PatientFrag", "NotesFragment created with patientId: " + patientId);

            recyclerView = view.findViewById(R.id.recyclerViewNotes);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            android.widget.Button btnAddNote = view.findViewById(R.id.btnAddNote);
            btnAddNote.setOnClickListener(v -> showNoteBottomSheet(null));

            fetchNotes();
        }

        private void showNoteBottomSheet(@Nullable com.rushi.healthcare_app.models.ConsultationNote existingNote) {
            ConsultationNoteBottomSheet bottomSheet = new ConsultationNoteBottomSheet();
            Bundle args = new Bundle();
            args.putString("PATIENT_ID", patientId);

            if (existingNote != null) {
                args.putString("NOTE_ID", existingNote.id);
                args.putString("SYMPTOMS", existingNote.symptoms);
                args.putString("OBSERVATIONS", existingNote.observations);
                args.putString("DIAGNOSIS", existingNote.diagnosis);
                args.putString("PLAN", existingNote.plan);
                args.putString("FOLLOW_UP_REQ", existingNote.follow_up_required);
                args.putString("FOLLOW_UP_DAYS", existingNote.follow_up_days);
            }

            bottomSheet.setArguments(args);
            bottomSheet.setOnSuccessCallback(this::fetchNotes);
            bottomSheet.show(getParentFragmentManager(), "ConsultationNoteBottomSheet");
        }

        private void fetchNotes() {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getNotes(patientId).enqueue(new Callback<ConsultationNoteResponse>() {
                @Override
                public void onResponse(Call<ConsultationNoteResponse> call, Response<ConsultationNoteResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().records != null) {
                        adapter = new NotesAdapter(response.body().records, new NotesAdapter.OnNoteActionListener() {
                            @Override
                            public void onEdit(com.rushi.healthcare_app.models.ConsultationNote note) {
                                showNoteBottomSheet(note);
                            }

                            @Override
                            public void onDelete(com.rushi.healthcare_app.models.ConsultationNote note) {
                                new android.app.AlertDialog.Builder(getContext())
                                        .setTitle("Delete Note")
                                        .setMessage("Are you sure you want to delete this consultation note?")
                                        .setPositiveButton("Delete", (dialog, which) -> deleteNoteFromDatabase(note.id))
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    }
                }
                @Override
                public void onFailure(Call<ConsultationNoteResponse> call, Throwable t) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_LONG).show();
                        Log.e("NotesFragment", "Fetch Error", t);
                    }
                }
            });
        }

        private void deleteNoteFromDatabase(String noteId) {
            java.util.HashMap<String, String> data = new java.util.HashMap<>();
            data.put("note_id", noteId);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.deleteNote(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Note deleted", Toast.LENGTH_SHORT).show();
                        fetchNotes();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete note", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    public static class VitalsFragment extends Fragment {
        private String patientId;
        private RecyclerView recyclerView;
        private VitalsAdapter adapter;
        private com.google.android.material.floatingactionbutton.ExtendedFloatingActionButton btnAddVitals;

        public static VitalsFragment newInstance(String patientId) {
            VitalsFragment fragment = new VitalsFragment();
            Bundle args = new Bundle();
            args.putString("PATIENT_ID", patientId);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public void onCreate(@Nullable Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            if (getArguments() != null) {
                patientId = getArguments().getString("PATIENT_ID");
            }
        }

        @Nullable
        @Override
        public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_vitals, container, false);
        }

        @Override
        public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
            super.onViewCreated(view, savedInstanceState);
            Log.d("PatientFrag", "VitalsFragment created with patientId: " + patientId);

            recyclerView = view.findViewById(R.id.recyclerViewVitals);
            recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

            btnAddVitals = view.findViewById(R.id.btnAddVitals);
            btnAddVitals.setOnClickListener(v -> showAddVitalsBottomSheet(null));

            fetchVitals();
        }

        private void showAddVitalsBottomSheet(@Nullable com.rushi.healthcare_app.models.VitalSign existingVital) {
            com.google.android.material.bottomsheet.BottomSheetDialog bottomSheetDialog =
                    new com.google.android.material.bottomsheet.BottomSheetDialog(requireContext());

            View bottomSheetView = LayoutInflater.from(getContext())
                    .inflate(R.layout.bottom_sheet_add_vitals, null);
            bottomSheetDialog.setContentView(bottomSheetView);

            android.widget.EditText inputSys = bottomSheetView.findViewById(R.id.inputSys);
            android.widget.EditText inputDia = bottomSheetView.findViewById(R.id.inputDia);
            android.widget.EditText inputHr = bottomSheetView.findViewById(R.id.inputHr);
            android.widget.EditText inputTemp = bottomSheetView.findViewById(R.id.inputTemp);
            android.widget.EditText inputWeight = bottomSheetView.findViewById(R.id.inputWeight);
            android.widget.Button btnSave = bottomSheetView.findViewById(R.id.btnSaveVitals);

            if (existingVital != null) {
                if (existingVital.blood_pressure != null && existingVital.blood_pressure.contains("/")) {
                    String[] bpParts = existingVital.blood_pressure.split("/");
                    if(bpParts.length == 2) {
                        inputSys.setText(bpParts[0]);
                        inputDia.setText(bpParts[1]);
                    }
                }
                inputHr.setText(existingVital.heart_rate);
                inputTemp.setText(existingVital.temperature);
                inputWeight.setText(existingVital.weight);
                btnSave.setText("Update Vitals");
            }

            btnSave.setOnClickListener(v -> {
                String sys = inputSys.getText().toString().trim();
                String dia = inputDia.getText().toString().trim();
                String hr = inputHr.getText().toString().trim();
                String temp = inputTemp.getText().toString().trim();
                String weight = inputWeight.getText().toString().trim();

                if (existingVital == null) {
                    saveVitalsToDatabase(sys, dia, hr, temp, weight, null, bottomSheetDialog);
                } else {
                    saveVitalsToDatabase(sys, dia, hr, temp, weight, existingVital.id, bottomSheetDialog);
                }
            });

            bottomSheetDialog.show();
        }

        private void saveVitalsToDatabase(String sys, String dia, String hr, String temp, String weight, @Nullable String vitalId, com.google.android.material.bottomsheet.BottomSheetDialog dialog) {
            java.util.HashMap<String, String> data = new java.util.HashMap<>();
            data.put("patient_id", patientId);
            data.put("bp_sys", sys);
            data.put("bp_dia", dia);
            data.put("hr", hr);
            data.put("temp", temp);
            data.put("weight", weight);

            ApiService apiService = RetrofitClient.getApiService();
            Call<Void> call;

            if (vitalId == null) {
                call = apiService.addVitals(data);
            } else {
                data.put("vital_id", vitalId);
                call = apiService.updateVitals(data);
            }

            call.enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), vitalId == null ? "Vitals added" : "Vitals updated", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        fetchVitals();
                    } else {
                        Toast.makeText(getContext(), "Failed to save vitals", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void deleteVitalsFromDatabase(String vitalId) {
            java.util.HashMap<String, String> data = new java.util.HashMap<>();
            data.put("vital_id", vitalId);

            ApiService apiService = RetrofitClient.getApiService();
            apiService.deleteVitals(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        Toast.makeText(getContext(), "Vitals deleted", Toast.LENGTH_SHORT).show();
                        fetchVitals();
                    } else {
                        Toast.makeText(getContext(), "Failed to delete vitals", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(getContext(), "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }

        private void fetchVitals() {
            ApiService apiService = RetrofitClient.getApiService();
            apiService.getVitals(patientId).enqueue(new Callback<VitalSignResponse>() {
                @Override
                public void onResponse(Call<VitalSignResponse> call, Response<VitalSignResponse> response) {
                    if (response.isSuccessful() && response.body() != null && response.body().records != null) {
                        adapter = new VitalsAdapter(response.body().records, new VitalsAdapter.OnVitalActionListener() {
                            @Override
                            public void onEdit(com.rushi.healthcare_app.models.VitalSign vital) {
                                showAddVitalsBottomSheet(vital);
                            }

                            @Override
                            public void onDelete(com.rushi.healthcare_app.models.VitalSign vital) {
                                new android.app.AlertDialog.Builder(getContext())
                                        .setTitle("Delete Vitals")
                                        .setMessage("Are you sure you want to delete this record?")
                                        .setPositiveButton("Delete", (dialog, which) -> deleteVitalsFromDatabase(vital.id))
                                        .setNegativeButton("Cancel", null)
                                        .show();
                            }
                        });
                        recyclerView.setAdapter(adapter);
                    }
                }
                @Override
                public void onFailure(Call<VitalSignResponse> call, Throwable t) {
                    if (getContext() != null) {
                        Toast.makeText(getContext(), "Failed to load vitals", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}