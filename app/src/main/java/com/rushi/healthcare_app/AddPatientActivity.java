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
import retrofit2.Retrofit;
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
        editFirstName = findViewById(R.id.editFirstName);
        editLastName = findViewById(R.id.editLastName);
        editDob = findViewById(R.id.editDob);
        editGender = findViewById(R.id.editGender);
        editPhone = findViewById(R.id.editPhone);
        editEmail = findViewById(R.id.editEmail);
        btnSavePatient = findViewById(R.id.btnSavePatient);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.1.16/healthcare-backend/api/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        setupLiveSearch();

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

    private void setupLiveSearch() {
        searchPatientAuto.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() >= 2) {
                    performSearch(s.toString());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        searchPatientAuto.setOnItemClickListener((parent, view, position, id) -> {
            Patient selected = searchResults.get(position);
            autoFillForm(selected);
        });
    }

    private void performSearch(String query) {
        // We use your existing GET index.php?search=query
        apiService.getPatients().enqueue(new Callback<PatientsListResponse>() {
            @Override
            public void onResponse(Call<PatientsListResponse> call, Response<PatientsListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    searchResults = response.body().getData();

                    // Filter locally based on the query for the dropdown
                    List<String> displayNames = new ArrayList<>();
                    for (Patient p : searchResults) {
                        if (p.getName().toLowerCase().contains(query.toLowerCase())) {
                            displayNames.add(p.getName() + " (" + p.getDob() + ")");
                        }
                    }

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddPatientActivity.this,
                            android.R.layout.simple_dropdown_item_1line, displayNames);
                    searchPatientAuto.setAdapter(adapter);
                    searchPatientAuto.showDropDown();
                }
            }

            @Override
            public void onFailure(Call<PatientsListResponse> call, Throwable t) {
                // Silently ignore search fails so as not to interrupt typing
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

        if (fname.isEmpty() || lname.isEmpty()) {
            Toast.makeText(this, "Name is required", Toast.LENGTH_SHORT).show();
            return;
        }

        // --- NEW MEDICAL ID LOGIC ---
        // Generates a random number between 000 and 999 (e.g., PT-007, PT-450)
        String generatedMedicalId = String.format(Locale.getDefault(), "PT-%03d", new Random().nextInt(1000));

        HashMap<String, String> map = new HashMap<>();
        map.put("medical_id", generatedMedicalId);
        map.put("first_name", fname);
        map.put("last_name", lname);
        map.put("date_of_birth", editDob.getText().toString());
        map.put("gender", editGender.getText().toString());
        map.put("phone", editPhone.getText().toString());
        map.put("email", editEmail.getText().toString());

        apiService.createPatient(map).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddPatientActivity.this, "Patient Saved as " + generatedMedicalId, Toast.LENGTH_LONG).show();
                    finish();
                } else {
                    Toast.makeText(AddPatientActivity.this, "Failed to save to database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                Toast.makeText(AddPatientActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
            }
        });
    }
}