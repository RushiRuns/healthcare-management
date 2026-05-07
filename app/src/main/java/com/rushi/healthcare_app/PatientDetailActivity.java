package com.rushi.healthcare_app;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PatientDetailActivity extends AppCompatActivity {

    private String patientId;
    private Patient currentPatient; // Store current data for pre-filling
    private TextView tvAvatar, tvPatientId, tvDemographics, tvBloodType, tvPhone, tvAllergies;
    private ImageView btnToolbarEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        tvAvatar = findViewById(R.id.avatar);
        tvPatientId = findViewById(R.id.patientId);
        tvDemographics = findViewById(R.id.patientDemographics);
        tvBloodType = findViewById(R.id.bloodType);
        tvPhone = findViewById(R.id.patientPhone);
        tvAllergies = findViewById(R.id.patientAllergies);
        btnToolbarEdit = findViewById(R.id.btnToolbarEdit);

        patientId = getIntent().getStringExtra("PATIENT_ID");
        if (patientId == null) patientId = "1";

        btnToolbarEdit.setOnClickListener(v -> showEditProfileBottomSheet());

        TabLayout tabLayout = findViewById(R.id.tabLayout);
        ViewPager2 viewPager = findViewById(R.id.viewPager);

        PatientPagerAdapter adapter = new PatientPagerAdapter(this, patientId);
        viewPager.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> {
            switch (position) {
                case 0: tab.setText("Overview"); break;
                case 1: tab.setText("Rx"); break;
                case 2: tab.setText("Notes"); break;
                case 3: tab.setText("Vitals"); break;
                case 4: tab.setText("Labs"); break;
            }
        }).attach();

        fetchPatientHeader();
    }

    private void showEditProfileBottomSheet() {
        BottomSheetDialog dialog = new BottomSheetDialog(this);
        View view = LayoutInflater.from(this).inflate(R.layout.bottom_sheet_edit_profile, null);
        dialog.setContentView(view);

        EditText etPhone = view.findViewById(R.id.inputEditPhone);
        EditText etDOB = view.findViewById(R.id.inputEditDOB);
        EditText etBlood = view.findViewById(R.id.inputEditBloodType);
        EditText etAllergies = view.findViewById(R.id.inputEditAllergies);
        Button btnSave = view.findViewById(R.id.btnSaveProfile);

        // Pre-fill data
        if (currentPatient != null) {
            etPhone.setText(currentPatient.getPhone());
            etDOB.setText(currentPatient.getDob());
            etBlood.setText(currentPatient.getBloodType().equals("N/A") ? "" : currentPatient.getBloodType());
            etAllergies.setText(currentPatient.getAllergiesSummary().equals("None") ? "" : currentPatient.getAllergiesSummary());
        }

        // DatePicker for DOB
        etDOB.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            new DatePickerDialog(this, android.R.style.Theme_DeviceDefault_Light_Dialog, (view1, year, month, dayOfMonth) -> {
                String selectedDate = String.format(Locale.getDefault(), "%04d-%02d-%02d", year, month + 1, dayOfMonth);
                etDOB.setText(selectedDate);
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
        });

        btnSave.setOnClickListener(v -> {
            HashMap<String, String> data = new HashMap<>();
            data.put("patient_id", patientId);
            data.put("phone", etPhone.getText().toString().trim());
            data.put("date_of_birth", etDOB.getText().toString().trim());
            data.put("blood_type", etBlood.getText().toString().trim());
            data.put("allergies", etAllergies.getText().toString().trim());

            RetrofitClient.getApiService().updatePatientProfile(data).enqueue(new Callback<Void>() {
                @Override
                public void onResponse(Call<Void> call, Response<Void> response) {
                    if (response.isSuccessful()) {
                        dialog.dismiss();
                        fetchPatientHeader(); // Refresh UI instantly
                    } else {
                        Toast.makeText(PatientDetailActivity.this, "Failed to update", Toast.LENGTH_SHORT).show();
                    }
                }
                @Override
                public void onFailure(Call<Void> call, Throwable t) {
                    Toast.makeText(PatientDetailActivity.this, "Network error", Toast.LENGTH_SHORT).show();
                }
            });
        });
        dialog.show();
    }

    private void fetchPatientHeader() {
        RetrofitClient.getApiService().getPatientDetails(patientId).enqueue(new Callback<PatientResponse>() {
            @Override
            public void onResponse(Call<PatientResponse> call, Response<PatientResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    currentPatient = response.body().getData();
                    if (currentPatient != null) {
                        if (currentPatient.getName() != null && !currentPatient.getName().isEmpty()) {
                            tvAvatar.setText(currentPatient.getName().substring(0, 1).toUpperCase());
                            if (getSupportActionBar() != null) getSupportActionBar().setTitle(currentPatient.getName());
                        }
                        tvPatientId.setText(currentPatient.getMedicalId());
                        tvDemographics.setText(currentPatient.getAge() + " • " + currentPatient.getGender());
                        tvBloodType.setText(currentPatient.getBloodType());
                        tvPhone.setText(currentPatient.getPhone());
                        tvAllergies.setText(currentPatient.getAllergiesSummary());
                    }
                } else {
                    // Add this log to see why the server rejected the request
                    android.util.Log.e("API_ERROR", "Response Error Code: " + response.code());
                    try {
                        android.util.Log.e("API_ERROR", "Response Error Body: " + response.errorBody().string());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
            @Override
            public void onFailure(Call<PatientResponse> call, Throwable t) {
                android.util.Log.e("PatientDetailActivity", "Header fetch failed", t);
            }
        });
    }
}