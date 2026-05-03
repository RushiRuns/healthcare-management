package com.rushi.healthcare_app;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.appbar.MaterialToolbar;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;
import android.app.DatePickerDialog;
import java.util.Calendar;
import java.util.Locale;
import java.util.Random;

public class AddPatientActivity extends AppCompatActivity {

    private AutoCompleteTextView searchPatientAuto;
    private TextInputEditText editFirstName, editLastName, editDob, editGender, editPhone, editEmail;
    private MaterialButton btnSavePatient;

    private ApiService apiService;
    private List<Patient> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_patient);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        topAppBar.setNavigationOnClickListener(v -> finish());

        searchPatientAuto = findViewById(R.id.searchPatientAuto);
        searchPatientAuto.setThreshold(1);
        searchPatientAuto.setOnClickListener(v -> searchPatientAuto.showDropDown());
        searchPatientAuto.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) searchPatientAuto.showDropDown();
        });
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editDob = findViewById(R.id.editDob);
        editGender = findViewById(R.id.editGender);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        btnSavePatient = findViewById(R.id.btnSavePatient);

        apiService = RetrofitClient.getApiService();

        loadPatientsForDropdown();

        // --- NEW DATE PICKER LOGIC ---
        editDob.setOnClickListener(v -> {
            Calendar cal = Calendar.getInstance();
            DatePickerDialog dialog = new DatePickerDialog(AddPatientActivity.this,
                    (view, year, month, dayOfMonth) -> {
                        // Formats date to YYYY-MM-DD for MySQL
                        String date = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                        editDob.setText(date);
                    }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));
            dialog.show();
        });

        btnSavePatient.setOnClickListener(v -> savePatientData());
    }

    private void loadPatientsForDropdown() {
        apiService.getPatients().enqueue(new Callback<PatientsListResponse>() {
            @Override
            public void onResponse(Call<PatientsListResponse> call, Response<PatientsListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResults = response.body().getData();
                    List<String> displayNames = new ArrayList<>();

                    for (Patient p : searchResults) {
                        displayNames.add(p.getName() + " (" + p.getMedicalId() + ")");
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddPatientActivity.this,
                            android.R.layout.simple_dropdown_item_1line, displayNames);
                    searchPatientAuto.setAdapter(adapter);

                    searchPatientAuto.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedText = (String) parent.getItemAtPosition(position);
                        for (Patient p : searchResults) {
                            if ((p.getName() + " (" + p.getMedicalId() + ")").equals(selectedText)) {
                                autoFillForm(p);
                                searchPatientAuto.setText(p.getName());
                                break;
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<PatientsListResponse> call, Throwable t) {
                // Silently fail, it just means autofill isn't available
            }
        });
    }

    private void autoFillForm(Patient patient) {
        String[] nameParts = patient.getName().split(" ", 2);
        editFirstName.setText(nameParts[0]);
        if (nameParts.length > 1) editLastName.setText(nameParts[1]);

        editDob.setText(patient.getDob());

        // --- NEW AUTO-FILL FIELDS ---
        if (patient.getGender() != null) editGender.setText(patient.getGender());
        if (patient.getPhone() != null) editPhone.setText(patient.getPhone());
        if (patient.getEmail() != null) editEmail.setText(patient.getEmail());

        Toast.makeText(this, "Existing record loaded!", Toast.LENGTH_SHORT).show();
    }

    private void savePatientData() {
        String fname = editFirstName.getText().toString().trim();
        String lname = editLastName.getText().toString().trim();
        String dob = editDob.getText().toString().trim();
        String phone = editPhone.getText().toString().trim();
        String genderInput = editGender.getText().toString().trim();

        if (fname.isEmpty() || lname.isEmpty() || dob.isEmpty() || phone.isEmpty()) {
            Toast.makeText(this, "Name, DOB, and Phone are required", Toast.LENGTH_LONG).show();
            return;
        }

        String gender = "Other";
        if (genderInput.toLowerCase().startsWith("m")) gender = "M";
        else if (genderInput.toLowerCase().startsWith("f")) gender = "F";

        String generatedMedicalId = "PT-" + System.currentTimeMillis();

        HashMap<String, String> map = new HashMap<>();
        map.put("medical_id", generatedMedicalId);
        map.put("first_name", fname);
        map.put("last_name", lname);
        map.put("date_of_birth", dob);
        map.put("gender", gender);
        map.put("phone", phone);
        map.put("email", editEmail.getText().toString().trim());

        apiService.createPatient(map).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddPatientActivity.this, "Patient Saved as " + generatedMedicalId, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddPatientActivity.this, "Failed to save (Ensure Phone is unique)", Toast.LENGTH_LONG).show();
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                Toast.makeText(AddPatientActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}