package com.rushi.healthcare_app;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.converter.gson.GsonConverterFactory;

public class AddAppointmentActivity extends AppCompatActivity {

    private AutoCompleteTextView searchPatientAuto;
    private TextInputEditText editDate, editTime, editReason;
    private MaterialButton btnSchedule;
    private String selectedPatientId = null;
    private ApiService apiService;
    private List<Patient> searchResults = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_appointment);

        MaterialToolbar topAppBar = findViewById(R.id.topAppBar);
        topAppBar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        topAppBar.setNavigationOnClickListener(v -> finish());

        searchPatientAuto = findViewById(R.id.searchPatientAuto);
        searchPatientAuto.setThreshold(0);
        searchPatientAuto.setOnClickListener(v -> searchPatientAuto.showDropDown());
        searchPatientAuto.setOnFocusChangeListener((v, hasFocus) -> {
            if (hasFocus) searchPatientAuto.showDropDown();
        });
        editDate = findViewById(R.id.editDate);
        editTime = findViewById(R.id.editTime);
        editReason = findViewById(R.id.editReason);
        btnSchedule = findViewById(R.id.btnSchedule);

        apiService = RetrofitClient.getApiService();

        setupPickers();

        // Fetch data once when activity starts
        loadPatientsForDropdown();

        btnSchedule.setOnClickListener(v -> saveAppointment());
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

                    ArrayAdapter<String> adapter = new ArrayAdapter<>(AddAppointmentActivity.this,
                            android.R.layout.simple_dropdown_item_1line, displayNames);
                    searchPatientAuto.setAdapter(adapter);

                    // Handle clicks properly from the natively filtered list
                    searchPatientAuto.setOnItemClickListener((parent, view, position, id) -> {
                        String selectedText = (String) parent.getItemAtPosition(position);
                        for (Patient p : searchResults) {
                            if ((p.getName() + " (" + p.getMedicalId() + ")").equals(selectedText)) {
                                selectedPatientId = p.getId();
                                searchPatientAuto.setText(p.getName()); // Clean up text after clicking
                                break;
                            }
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<PatientsListResponse> call, Throwable t) {
                Toast.makeText(AddAppointmentActivity.this, "Failed to load patient list", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setupPickers() {
        editDate.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new DatePickerDialog(this, (view, y, m, d) ->
                    editDate.setText(String.format(Locale.US, "%04d-%02d-%02d", y, m + 1, d)),
                    c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH)).show();
        });

        editTime.setOnClickListener(v -> {
            Calendar c = Calendar.getInstance();
            new TimePickerDialog(this, (view, h, m) ->
                    editTime.setText(String.format(Locale.US, "%02d:%02d:00", h, m)),
                    c.get(Calendar.HOUR_OF_DAY), c.get(Calendar.MINUTE), true).show();
        });
    }

    private void saveAppointment() {
        if (selectedPatientId == null) {
            Toast.makeText(this, "Please select a patient", Toast.LENGTH_SHORT).show();
            return;
        }

        HashMap<String, Object> map = new HashMap<>();
        map.put("patient_id", selectedPatientId);
        map.put("doctor_id", 1); // Hardcoded doctor for now
        map.put("appointment_date", editDate.getText().toString() + " " + editTime.getText().toString());
        map.put("reason_for_visit", editReason.getText().toString());
        map.put("status", "scheduled");
        map.put("duration_minutes", 30);

        apiService.createAppointment(map).enqueue(new Callback<AppointmentResponse>() {
            @Override
            public void onResponse(Call<AppointmentResponse> call, Response<AppointmentResponse> response) {
                if (response.isSuccessful()) {
                    Toast.makeText(AddAppointmentActivity.this, "Appointment Scheduled!", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            @Override public void onFailure(Call<AppointmentResponse> call, Throwable t) {}
        });
    }
}